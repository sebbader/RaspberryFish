package de.fhg.iais.eis.ids.speakingfish.raspberry.gpio;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.uni.bonn.semantic.lab.raspberry.GPIOOutputPin;

import de.fhg.iais.eis.ids.connector.util.RdfHelper;
import de.fhg.iais.eis.ids.connector.vocab.RdfNamespace;
import edu.kit.aifb.step.web.api.WebResource;

/**
 * 
 * class representing and handling all interactions with 
 * the raspberry gpio pins
 * 
 * @author sbader
 *
 */
public class GpioWebEndpoint implements WebResource {

	final Logger logger = LoggerFactory.getLogger(GpioWebEndpoint.class);
	
			
	@Autowired
	private RdfHelper helper;


	// identifies the gpio pin of the raspberry
	// serves as an identifier for the class instances
	private int gpio;
	
	private GPIOOutputPin gpioOutputPin;

	private static Node[] gpioStates;

	// the url under which this resource is accessible
	private URL url;



	public GpioWebEndpoint(int gpio, URL url) {

		this.gpio = gpio;
		//TODO activate access to GPIO
		//this.gpioOutputPin = new GPIOOutputPin(RaspiPin.getPinByAddress(gpio), PinState.LOW);
		this.url = url;

		// initialize a static memory of the gpio states
		if (gpioStates == null) {
			gpioStates = new Node[32];
			for (int i = 0; i < gpioStates.length; i++) 
				gpioStates[i] = new Resource(RdfNamespace.saref + "Off");
		}
	}


	private Iterable<Node[]> getSelfDescription() {

		List<Node[]> selfDescription = new ArrayList<Node[]>();
		Node thisGpio = new Resource( this.url.toString() + "#thisGpio");
		selfDescription.add(new Node[] { thisGpio, RDF.TYPE, new Resource(RdfNamespace.saref + "Actuator") });
		selfDescription.add(new Node[] { thisGpio, RDFS.LABEL, new Literal("GPIO " + this.gpio) });
		selfDescription.add(new Node[] { thisGpio, RDFS.COMMENT, new Literal("This represents a GPIO pin at the raspberry pi. "
				+ "You can either read (GET) its current state (on/off) or overwrite it (PUT) by sending triples containing "
				+ "e.g. '[] saref:hasState saref:on/off .'", "en") });
		selfDescription.add(new Node[] { thisGpio, new Resource(RdfNamespace.saref + "hasState"), this.gpioStates[this.gpio] });
		
		return selfDescription;
	}
	
	
	@Override
	public Response doDelete() {
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).allow("GET", "PUT").entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {  }).build();
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
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).allow("GET", "PUT").entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {  }).build();
	}

	@Override
	public Response doPost(Iterable<Node[]> arg0) {
		return Response.status(Response.Status.METHOD_NOT_ALLOWED).allow("GET", "PUT").entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {  }).build();
	}

	@Override
	public Response doPut(Iterable<Node[]> inputGraph) {

		Model inputModel = helper.parseNxNodeToJenaModel(inputGraph);
//		String sparqlQuery = "Select ?newState where {"
//				+ "?x <" + RdfNamespace.saref + "hasState> ?newState ."
//				+ "} LIMIT 1";
//		Query hasSarefStateTriple = QueryFactory.create(sparqlQuery);
//		ResultSet results = hasSarefStateTriple.;
		if (inputModel.contains(null, ResourceFactory.createProperty(RdfNamespace.saref + "hasState"), null, null)) {
			
			RDFNode hasStateObject = inputModel.listObjectsOfProperty(ResourceFactory.createProperty(RdfNamespace.saref + "hasState")).next();
			Node hasStateObjectAsNxNode = new Resource(hasStateObject.toString());
			
			if (hasStateObjectAsNxNode.toString().toLowerCase().contains("off")) {
				
				gpioOutputPin.setState(PinState.LOW);
				this.gpioStates[this.gpio] = hasStateObjectAsNxNode;
	
			} else if (hasStateObjectAsNxNode.toString().toLowerCase().contains("on")) {

				gpioOutputPin.setState(PinState.HIGH);
				this.gpioStates[this.gpio] = hasStateObjectAsNxNode;
			
			} else {
				// not allowed
				return Response.status(Status.BAD_REQUEST).entity("Check saref:hasState object.").build();
			}
			
			
			return doGet();
		}

		return Response.status(Status.BAD_REQUEST).build();
	}

	@Override
	public WebResource retrieve(String child) {
		// no further child resources 
		return null;
	}

}
