/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import hera.util.Base64Utils;
import hera.util.Sha256Utils;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.VerifierImpl;
import io.aergo.openkeychain.core.Verifier;

public class KeyManagerTest {
	
	@Test
	public void testInMemoryKeyManager() {
		String address = "AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm";
		String rawPrivateKey = "mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=";
		KeyManager km = new SimpleKeyManager(rawPrivateKey);
		assertEquals(address, km.fetchAddress());
		
		String encodedSignature = "MEQCIEjZwAsL4k5DS1acY5KrbjzY6jhtrJ4gza2z9IDXJeGHAiBU2uDbWyNbXpUb/2wJY4lor9kbAdADNuyY4YrH5LIYMg==";
		String sampleMessage = "sample";
		byte[] hashedMessage = Sha256Utils.digest(sampleMessage.getBytes(StandardCharsets.UTF_8));
		byte[] signature = km.getSigner().sign(hashedMessage);
		assertEquals(encodedSignature, Base64Utils.encode(signature));
		
		Verifier verifier = km.getVerifier();
		assertTrue(verifier.verify(hashedMessage, signature));
	}
	
	@Test
	public void testVerify() {
		String address = "AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm";
		
		String encodedSignature = "MEQCIEjZwAsL4k5DS1acY5KrbjzY6jhtrJ4gza2z9IDXJeGHAiBU2uDbWyNbXpUb/2wJY4lor9kbAdADNuyY4YrH5LIYMg==";
		String sampleMessage = "sample";
		byte[] hashedMessage = Sha256Utils.digest(sampleMessage.getBytes(StandardCharsets.UTF_8));
		byte[] signature = Base64Utils.decode(encodedSignature);
		
		Verifier verifier = new VerifierImpl(address);
		assertTrue(verifier.verify(hashedMessage, signature));
	}

}
