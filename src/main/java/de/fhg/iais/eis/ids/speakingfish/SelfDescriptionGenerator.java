package de.fhg.iais.eis.ids.speakingfish;


import de.fraunhofer.iais.eis.BaseConnectorBuilder;
import de.fraunhofer.iais.eis.CatalogBuilder;
import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.HostBuilder;
import de.fraunhofer.iais.eis.InteractiveEndpoint;
import de.fraunhofer.iais.eis.InteractiveEndpointBuilder;
import de.fraunhofer.iais.eis.OperationBindingBuilder;
import de.fraunhofer.iais.eis.Participant;
import de.fraunhofer.iais.eis.ParticipantBuilder;
import de.fraunhofer.iais.eis.PredefinedSecurityProfile;
import de.fraunhofer.iais.eis.Protocol;
import de.fraunhofer.iais.eis.PublicKeyBuilder;
import de.fraunhofer.iais.eis.Resource;
import de.fraunhofer.iais.eis.ResourceBuilder;
import de.fraunhofer.iais.eis.SecurityProfileBuilder;
import de.fraunhofer.iais.eis.util.ConstraintViolationException;
import de.fraunhofer.iais.eis.util.PlainLiteral;
import springfox.documentation.builders.OperationBuilder;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

@Component
public class SelfDescriptionGenerator {


	@Value("${connector.url}")
	private String CONN_BASE_URL = "";

	@Value("${connector.participant}")
	private String PARTICIPANT_URL = "";



	public Connector generate() throws URISyntaxException, ConstraintViolationException, IOException
	{

		Participant iais = new ParticipantBuilder(new URL(PARTICIPANT_URL)).build();

		Connector connector = new BaseConnectorBuilder(new URL(CONN_BASE_URL))
				._descriptions_(new ArrayList<PlainLiteral>() {{
					add(new PlainLiteral("This is a demo service to develop a negotiation handshake based on user preferences.", "en"));
				}})
				._maintainer_(iais.getId())
				._inboundModelVersions_(new ArrayList<String>() {{add("1.0.1");}})
				._outboundModelVersion_("1.0.1")
				._curator_(iais.getId())
				._publicKey_(new PublicKeyBuilder()
						._keyType_(null)
						._keyValue_(new String("-----BEGIN CERTIFICATE REQUEST-----"
								+ "MIIB4DCCAUkCAQAwgZ8xCzAJBgNVBAYTAmRlMRswGQYDVQQIDBJCYWRlbi1Xw7xy"
								+ "dHRlbWJlcmcxGDAWBgNVBAoMD0ZyYXVuaG9mZXIgSU9TQjEiMCAGA1UEAwwZaHR0"
								+ "cDovL2lvc2IuZnJhdW5ob2Zlci5kZTE1MDMGCSqGSIb3DQEJARYmbGppbGphbmEu"
								+ "c3RvamFub3ZpY0Bpb3NiLmZyYXVuaG9mZXIuZGUwgZ8wDQYJKoZIhvcNAQEBBQAD"
								+ "gY0AMIGJAoGBAL0pQnzidiv3/Yfj6wj4Re4xO2JV1k/D0rwgZCCTWuoknYBamiIi"
								+ "XBE36Di/zx3APcTHR+7JyPMr/RTIcswsJ1opniAhhW5VeDMjTu3DMl2AbDoouXrp"
								+ "go6d8SXZMJkHppGDMUpD/Lrv0tnHDHRmkWY6lZ53NB+kR7JyjcMdsOg5AgMBAAGg"
								+ "ADANBgkqhkiG9w0BAQ0FAAOBgQBKkkKbcl1GI8hua/LD4ianRh/A2zgzPYhsAO8Y"
								+ "vB6Fr750DkVEC5w2yX+QgiuvkI2e70Vl1sgjX7peR0WU5kK7EYsTgaEOVXkUqs1e"
								+ "AHwMlqjMDTFipZBMTTd2zl/18dLPQfp+mbqKXs3WF/WIE1zcAToLbU/1c2rM2Ted"
								+ "OXte2A=="
								+ "-----END CERTIFICATE REQUEST-----").getBytes())
						.build())

				._catalog_(new CatalogBuilder()._offers_(new ArrayList<Resource>() {{
					add(new ResourceBuilder()
							._descriptions_(new ArrayList<PlainLiteral>() {{
								add(new PlainLiteral("This is a demo service to develop a negotiation handshake based on user preferences.", "en")); 
							}})
							// TODO: Operation 
//							._resourceEndpoints_( new ArrayList<InteractiveEndpoint>() {{ 
//								add( new InteractiveEndpointBuilder()
//										._inboundPath_("raspberry")
//										._endpointHost_(new HostBuilder( new URL(CONN_BASE_URL) )._accessUrl_(new URI(CONN_BASE_URL + "/raspberry"))._protocol_(Protocol.HTTP2).build())
//										._endpointOperationBinding_(new OperationBindingBuilder()
//												._boundOperation_(null)
//												._apiDocument_(new ResourceBuilder().build())
//												.build())
//										.build()); 
//							}})
							.build());
				}})
						.build())
				._securityProfile_(new SecurityProfileBuilder()._basedOn_(PredefinedSecurityProfile.LEVEL0SECURITYPROFILE).build())
				.build();



		return connector;
	}



}
