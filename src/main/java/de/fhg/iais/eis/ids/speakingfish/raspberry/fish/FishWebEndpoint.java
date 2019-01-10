package de.fhg.iais.eis.ids.speakingfish.raspberry.fish;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;

import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.semanticweb.yars.nx.namespace.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import de.fhg.iais.eis.ids.connector.vocab.RdfNamespace;
import edu.kit.aifb.step.web.api.WebResource;

public class FishWebEndpoint implements WebResource {

	final Logger logger = LoggerFactory.getLogger(FishWebEndpoint.class);


	@Value("${fish.bodygpio}")
	private String bodygpio = "";

	@Value("${fish.mouthgpio}")
	private String mouthgpio = "";

	private URL url;

	private static String text = "";
	private static boolean isCurrentlyActive = false;


	/**
	 * This is the default constructor. Should not be used but rather the one with a proper baseURL assignement
	 */
	public FishWebEndpoint() {
		try {
			this.url = new URL("http://example.org/raspberry/fish");
		} catch (MalformedURLException e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

	/**
	 * Use this constructor. 
	 */
	public FishWebEndpoint(URL url) {
		this.url = url;
	}

	
	
	private Iterable<Node[]> getSelfDescription() {

		List<Node[]> selfDescription = new ArrayList<Node[]>();
		Node thisFish = new Resource( this.url.toString() + "#thisFish");
		selfDescription.add(new Node[] { thisFish, RDF.TYPE, new Resource(RdfNamespace.saref + "Actuator") });
		selfDescription.add(new Node[] { thisFish, RDFS.LABEL, new Literal("Speaking Fish") });
		selfDescription.add(new Node[] { thisFish, RDFS.COMMENT, new Literal("This resource represents a speaking fish "
				+ "connected to the raspberry pi (../). "
				+ "You can either read (GET) its current state or send (POST/PUT) new text to read by sending triples containing "
				+ "e.g. '[] ex:read 'text to read'@en .'", "en") });

		
		Node state;
		if (this.isCurrentlyActive) {
			state = new Resource(RdfNamespace.saref + "On");
			selfDescription.add(new Node[] { thisFish, new Resource("http:example.org/read"), new Literal(text, XSD.STRING) });
		} else {
			state = new Resource(RdfNamespace.saref + "Off");
		}
		selfDescription.add(new Node[] { thisFish, new Resource(RdfNamespace.saref + "hasState"), state });
		

		return selfDescription;
	}


	@Override
	public Response doDelete() {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET", "PUT", "POST")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	
	@Override
	public Response doGet() {
		return Response.ok().entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {  } ).build();

	}

	@Override
	public Response doHead() {
		return doGet();
	}

	@Override
	public Response doOptions() {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET", "PUT", "POST")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	@Override
	public Response doPost(Iterable<Node[]> inputNodes) {
		return doPut(inputNodes);
	}

	@Override
	public Response doPut(Iterable<Node[]> inputNodes) {

		//TODO extract [] ex:read ?text  triple
		
		//TODO send ?text to audio
		
		//TODO calculate how long reading will take
		
		//TODO activate mouthGpio and bodyGpio for n seconds

		return doGet();
	}

	@Override
	public WebResource retrieve(String child) {
		// no child resources
		return null;
	}

}
