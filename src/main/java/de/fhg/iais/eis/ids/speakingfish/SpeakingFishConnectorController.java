package de.fhg.iais.eis.ids.speakingfish;



import de.fhg.iais.eis.ids.connector.communication.DapsService;
import de.fhg.iais.eis.ids.connector.messagehandling.MessageDispatcher;
import de.fhg.iais.eis.ids.connector.util.MessageCreator;
import de.fhg.iais.eis.ids.connector.util.RdfHelper;
import de.fhg.iais.eis.ids.speakingfish.raspberry.RaspberryWebEndpoint;
import de.fraunhofer.iais.eis.Message;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import edu.kit.aifb.step.web.api.WebResource;

import org.apache.commons.io.FileUtils;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RiotException;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.parsers.external.json.jsonld_java.JsonLDparser;
import org.semanticweb.yars.utils.CallbackIterator;
import org.semanticweb.yars.utils.ErrorHandlerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

//@RestController
@Component
@Path("/")
public class SpeakingFishConnectorController {

	final Logger logger = LoggerFactory.getLogger(SpeakingFishConnectorController.class);


	@Value("${connector.url}")
	private String connectorUrl = "";

	@Value("${connector.broker}")
	private String broker = "";

	@Value("${idslab.echoservice}")
	private String echo = "";

	@Value("${idslab.daps}")
	private String dapsUrl = "";

	@Value("${connector.modelversion}")
	private String modelversion = "";



	@Autowired
	private SelfDescriptionGenerator selfDescriptionGenerator;

	@Autowired
	private MessageCreator messageCreator;

	@Autowired
	private MessageDispatcher messageDispatcher;

	//	@Autowired
	//	private RdfHelper helper;

	@Autowired
	private DapsService dapsService;



	/**
	 * Startup routine
	 */
	public SpeakingFishConnectorController() {


	}



	@EventListener(ApplicationReadyEvent.class)
	public void prepare() throws IOException, URISyntaxException {
		//TODO check if this is still executed in Spring and Jersey

		//	dapsService.getToken();
		//		registerAtBroker();
		//		sayHelloToEcho();
		//		brokerService.queryBrokerForConnectors();
	}


	/**
	 * Shutdown process to store userprofiles on disk
	 */
	@PreDestroy
	@EventListener(ContextStoppedEvent.class)
	public void onDestroy() {
		//TODO check if this is still executed in Spring and Jersey

		// ShutdownHook to write user profiles to disk
		final Thread mainThread = Thread.currentThread();
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {

				try {

					mainThread.join();

				} catch (Exception e) {
					e.printStackTrace();
					logger.warn(String.valueOf(e), e);
				} 
			}
		});
	}





	/*
	 * basic resources
	 */
	//	@RequestMapping(
	//			value = "/",
	//			method = RequestMethod.GET,
	//			produces = {"application/ld+json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8_VALUE}
	//			)
	//	public @ResponseBody String root() throws ConstraintViolationException, IOException, URISyntaxException {
	//		logger.debug("Incoming GET /");
	//		return selfDescriptionGenerator.generate().toRdf();
	//	}
	@RequestMapping(
			value = "/",
			method = RequestMethod.GET,
			produces = {"application/ld+json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8_VALUE}
			)
	@GET
	@Path("/")
	public Response requestRootResource() throws ConstraintViolationException, IOException, URISyntaxException {
		logger.debug("Incoming GET /");

		RdfHelper helper = new RdfHelper();

		return Response.ok().entity( helper.jsonLd2GenericEntity(selfDescriptionGenerator.generate().toRdf(), new URI("http://example.org/")) ).build();
	}

	@RequestMapping(
			value = "/about",
			method = RequestMethod.GET,
			produces = {"application/ld+json;charset=UTF-8", MediaType.APPLICATION_JSON_UTF8_VALUE}
			)
	@GET
	@Path("/about")
	public @ResponseBody String aboutJsonLD() throws ConstraintViolationException, IOException, URISyntaxException {
		logger.debug("Incoming GET /about");
		return selfDescriptionGenerator.generate().toRdf();
	}

	@RequestMapping(
			value = "/about",
			method = RequestMethod.GET,
			produces = {MediaType.TEXT_HTML_VALUE}
			)
	public @ResponseBody ResponseEntity<String> aboutHtml() throws ConstraintViolationException, IOException, URISyntaxException {
		logger.debug("Incoming GET /about");
		String html = "<html><title>Connector Description</title><body>" + selfDescriptionGenerator.generate().toRdf() + "</body></html>";
		logger.debug("Requesting self-description at /about");
		return new ResponseEntity<String>(html, HttpStatus.OK);
	}


	/*
	 * 
	 * Raspberry virtual twin/endpoint
	 * 
	 */
	@Path("raspberry")
	public WebResource httpRaspberry() {
		return new RaspberryWebEndpoint(connectorUrl);
	}

}
