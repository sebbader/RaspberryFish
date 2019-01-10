package de.fhg.iais.eis.ids.speakingfish.raspberry;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Link.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.semanticweb.yars.nx.Literal;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.namespace.LDP;
import org.semanticweb.yars.nx.namespace.RDF;
import org.semanticweb.yars.nx.namespace.RDFS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import de.fhg.iais.eis.ids.connector.vocab.RdfNamespace;
import de.fhg.iais.eis.ids.speakingfish.raspberry.fish.FishWebEndpoint;
import de.fhg.iais.eis.ids.speakingfish.raspberry.gpio.GpioWebEndpoint;
import edu.kit.aifb.step.web.api.WebResource;


public class RaspberryWebEndpoint implements WebResource {

	private final int maxGpio = 32;
	
	private String connectorUrl;

	final Logger logger = LoggerFactory.getLogger(RaspberryWebEndpoint.class);
	
	
	public RaspberryWebEndpoint(String connectorUrl) {
		
		this.connectorUrl = connectorUrl;
//		
//		try {
//			
//			if (connectorUrl.endsWith("/")) this.connectorUrl = connectorUrl.substring(0, connectorUrl.length() - 2);
//			this.url = new URL(connectorUrl + "/raspberry");
//			
//		} catch (MalformedURLException e) {
//			logger.error("Invalid connector url parameter. Check properties file.", e);
//			e.printStackTrace();
//		}
	
	}
	
	
	private Iterable<Node[]> getSelfDescription() {

		List<Node[]> selfDescription = new ArrayList<Node[]>();
		Node thisRaspberry = new Resource( connectorUrl + "/raspberry" + "#thisRaspberry");
		selfDescription.add(new Node[] { thisRaspberry, RDF.TYPE, new Resource(RdfNamespace.ssn + "Device") });
		selfDescription.add(new Node[] { thisRaspberry, RDFS.LABEL, new Literal("Sebastians Raspberry") });
		selfDescription.add(new Node[] { thisRaspberry, RDFS.COMMENT, new Literal("", "en") });
		for (int i = 0; i <= 32; i++) selfDescription.add(new Node[] { thisRaspberry, LDP.CONTAINS, new Resource(connectorUrl + "/raspberry/" + i) });
		selfDescription.add(new Node[] { thisRaspberry, LDP.CONTAINS, new Resource(connectorUrl + "/raspberry/fish") });
		
		return selfDescription;
	}
	

	@Override
	public Response doDelete() {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	@Override
	public Response doGet() {
		return Response
				.ok().entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} ).build();
	}

	@Override
	public Response doHead() {
		return doGet();
	}

	@Override
	public Response doOptions() {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	@Override
	public Response doPost(Iterable<Node[]> arg0) {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	@Override
	public Response doPut(Iterable<Node[]> arg0) {
		return Response
				.status(Response.Status.METHOD_NOT_ALLOWED)
				.allow("GET")
				.entity(new GenericEntity<Iterable<Node[]>>( getSelfDescription() ) {} )
				.build();
	}

	@Override
	public WebResource retrieve(String child) {
		
		if (child.equalsIgnoreCase("fish")) {

			if (connectorUrl.endsWith("/")) this.connectorUrl = connectorUrl.substring(0, connectorUrl.length() - 2);
			
			try {
				return new FishWebEndpoint( new URL(connectorUrl + "/raspberry/fish"));
			} catch (MalformedURLException e) {
				logger.error("Problems parsing URL properly:" + connectorUrl + "/raspberry/fish", e);
				e.printStackTrace();
				return null;
			}
		}
		

		try {
			int gpio = Integer.parseInt(child);
			
			if (connectorUrl.endsWith("/")) this.connectorUrl = connectorUrl.substring(0, connectorUrl.length() - 2);

			if (0 <= gpio && gpio <= maxGpio) {
				try {
					return new GpioWebEndpoint(gpio, new URL(connectorUrl + "/raspberry/" + gpio));
				} catch (MalformedURLException e) {
					logger.error("Problems parsing URL properly:" + connectorUrl + "/raspberry/" + gpio, e);
					e.printStackTrace();
					return null;
				}
			} else {
				return null;
			}

		} catch (NumberFormatException e) {
			logger.error("Could not find proper GPIO web resource for path raspberry/" + child);
			return null;
		}

	}


}
