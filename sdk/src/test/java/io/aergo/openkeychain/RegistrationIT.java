/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.aergo.openkeychain.backend.Backend;
import io.aergo.openkeychain.backend.MockBackend;
import io.aergo.openkeychain.client.LoginManager;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.Publishers;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.core.SimpleKeyManager;
import io.aergo.openkeychain.core.SimplePublishers;
import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Entry;
import io.aergo.openkeychain.model.SimpleMetadata;
import io.aergo.openkeychain.model.Response;
import io.aergo.openkeychain.server.RegistrationManager;

public class RegistrationIT {
	
	static final Logger logger = LoggerFactory.getLogger(RegistrationIT.class);
	
	
	final String serverRawPrivateKey = "JiOLST6+1g3+NFYVOHx9Ewpmn/LbRL2yzaiYllj/T98=";
	final String serverAddress = "AmMvrwyt6D4KLzoZKaJzjW3HRsLFbzPpWxTkZdNd8eBxfw7hMaqy";
	final String[] serverAddresses = new String[] {
			serverAddress,
			"AmMvoVJsiZQ9WtZHpHB3SctYicJB65cQ5tZE1LPDdzNSVquhCmD9", // l/k4L8f6Xp50NZaRCR/mLzEamaORm80HOFY0TKJZP5I=
			"AmQ7XdAXq5z7csoHv1V8Jzqi7Y6HdKPvGpN468Q7v7qpms9wyMSv", // D1vPGa1IV1PjOFWkqzmiTAy92Z6gDb5xjxvIwPNr/js=
			"AmMffLJhr8WdYk7rzbYLV9gRdzNEjcteJPVjBEwqwQkN8AKaKuKT", // scrIju+hgcew5c6iHsPWCu+5x/OxYjGNoSBMdaCCNew=
			"AmNjHuCwAgxBW3dEPgRqVsVKjh7ZjwcakhzkmoG29ZuXEpcCWK6t", // tTVW9bcfvT2LsR502A0H3EgClzhzYgFWaHMuLZOVzPo=
			"AmLZkykDfWPnL7oiHHrxvFRVaoMRy7uvTh7nQLo26WqY5LMdKJtx", // kd6ssEvD6K5K2dpwL0R7NeIUJ+nWteYNuUP8D9q4/4E=
	};
	
	
	// server
	Backend mockBackend;
	
	// client
	KeyManager clientKeyManager;
	
	@Before
	public void before() throws IOException {
		// prepare for server
		this.mockBackend = new MockBackend(serverAddress);
		
		// prepare for client
		this.clientKeyManager = new SimpleKeyManager();
	}
	
	@After
	public void after() throws IOException {
	}
	
	
	@Test
	public void testRegistration() throws IOException {
		testRecordRegistrationIT();
		testCheckRegistrationIT(true);
		testRevokeRegistrationIT();
		testCheckRegistrationIT(false);
	}
	
	
	public void testRecordRegistrationIT() throws IOException {
		// [    server] prepare server
		Signer signer = new SimpleKeyManager(serverRawPrivateKey).getSigner();
		RegistrationManager regManager = RegistrationManager.builder()
				.backend(mockBackend)
				.publishers(serverAddresses)
				.signer(signer)
				.build();
		
		// [client    ] prepare client and request challenge
		LoginManager loginManager = LoginManager.builder()
				.publishers(serverAddresses)
				.build();
		
		// [    server] create challenge
		String challengeSerialized = regManager.createChallenge().marshal();
		
		// [client    ] receive challenge
		Challenge challenge = Challenge.unmarshal(challengeSerialized);
		
		// [client    ] check challenge
		assertTrue(loginManager.checkChallenge(challenge));
		
		// [client    ] create response
		String responseSerialized = loginManager.createResponse(challenge, clientKeyManager.getSigner()).marshal();
		
		// [    server] receive response
		Response response = Response.unmarshal(responseSerialized);
		
		// [    server] check response
		assertTrue(regManager.checkResponse(response));
		
		// [    server] record registration\
		regManager.recordRegistration(Entry.of(response.getCertificate(), SimpleMetadata.of("sample")));
		logger.debug("record: {}", mockBackend.fetchRegistration(response.getCertificate()));
	}
	
	
	public void testCheckRegistrationIT(final boolean result) throws IOException {
		// [    server] prepare server
		Signer signer = new SimpleKeyManager(serverRawPrivateKey).getSigner();
		RegistrationManager regManager = RegistrationManager.builder()
				.backend(mockBackend)
				.publishers(serverAddresses)
				.signer(signer)
				.build();
		
		// [client    ] prepare client and request challenge
		LoginManager loginManager = LoginManager.builder()
				.publishers(serverAddresses)
				.signer(clientKeyManager.getSigner())
				.build();
		
		// [    server] create challenge
		String challengeSerialized = regManager.createChallenge().marshal();
		
		// [client    ] receive challenge
		Challenge challenge = Challenge.unmarshal(challengeSerialized);
		
		// [client    ] check challenge
		assertTrue(loginManager.checkChallenge(challenge));
		
		// [client    ] create response
		String responseSerialized = loginManager.createResponse(challenge).marshal();
		
		// [    server] receive response
		Response response = Response.unmarshal(responseSerialized);
		
		// [    server] check response
		assertTrue(regManager.checkResponse(response));
		
		// [    server] check registration
		logger.debug("result: {}", result);
		logger.debug("response.getCertificate(): {}", response.getCertificate());
		assertEquals(result, regManager.checkRegistration(response.getCertificate()));
		logger.debug("fetch: {}", regManager.fetchRegistration(response.getCertificate()));
	}
	
	
	public void testRevokeRegistrationIT() throws IOException {
		// [    server] prepare server
		Publishers publishers = new SimplePublishers(serverAddresses);
		Signer signer = new SimpleKeyManager(serverRawPrivateKey).getSigner();
		RegistrationManager regManager = RegistrationManager.builder()
				.backend(mockBackend)
				.publishers(publishers)
				.signer(signer)
				.build();
		
		// [client    ] prepare client and request challenge
		LoginManager loginManager = LoginManager.builder()
				.publishers(serverAddresses)
				.signer(clientKeyManager.getSigner())
				.build();
		
		// [    server] create challenge
		String challengeSerialized = regManager.createChallenge().marshal();
		
		// [client    ] receive challenge
		Challenge challenge = Challenge.unmarshal(challengeSerialized);
		
		// [client    ] check challenge
		assertTrue(loginManager.checkChallenge(challenge));
		
		// [client    ] create response
		String responseSerialized = loginManager.createResponse(challenge).marshal();
		
		// [    server] receive response
		Response response = Response.unmarshal(responseSerialized);
		
		// [    server] check response
		assertTrue(regManager.checkResponse(response));
		
		// [    server] revoke registration
		regManager.revokeRegistration(response.getCertificate());
	}
	
}
