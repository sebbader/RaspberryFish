package de.fhg.iais.eis.ids.speakingfish;

import org.glassfish.jersey.server.ResourceConfig;
import org.semanticweb.yars.jaxrs.JsonLdMessageBodyReaderWriter;
import org.semanticweb.yars.jaxrs.NxMessageBodyReaderWriter;
import org.semanticweb.yars.jaxrs.RdfXmlMessageBodyWriter;
import org.semanticweb.yars.jaxrs.TurtleMessageBodyReader;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import de.fhg.iais.eis.ids.connector.messagehandling.MessageDispatcher;
import de.fhg.iais.eis.ids.connector.util.RdfHelper;


/**
 * 
 * The Jersey Config class contains the necessary registers 
 * to load all required classes for Jersey. Especially the
 * Controller class for Spring and the MessageBodyReader and 
 * MessageBodyWriter, which parse and serialize the incoming
 * RDF HTTP payloads.
 * 
 * @author sbader
 *
 */
@Component
@Configuration
@ComponentScan("de.fhg.iais.eis.ids.connector.util")
@ComponentScan("de.fhg.iais.eis.ids.connector.messagehandling")
@ComponentScan("de.fhg.iais.eis.ids.connector.communication")
public class JerseyConfig extends ResourceConfig {
	
    public JerseyConfig() {

        registerEndpoints();

    }

    private void registerEndpoints() {

    	// MessageBodyReader and MessageBodyWriter for HTTP 
    	// payload <-> Java nx-objects
    	super.register(JsonLdMessageBodyReaderWriter.class);
    	super.register(TurtleMessageBodyReader.class);
    	super.register(NxMessageBodyReaderWriter.class); 
    	super.register(RdfXmlMessageBodyWriter.class);
    	
    	super.register(MessageDispatcher.class);
    	super.register(RdfHelper.class);
    	super.register(SpeakingFishConnectorController.class);

    }

}
