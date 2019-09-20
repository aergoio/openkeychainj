/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.util.Base64Utils;

public class KeyUtilsTest {
	
	@Test
	public void testCreateRawPrivateKey() {
		final AergoKey key1 = new AergoKeyGenerator().create();
		byte[] raw1 = KeyUtils.exportRawPrivateKey(key1.getPrivateKey());
		final AergoKey key2 = KeyUtils.toAergoKey(raw1);
		byte[] raw2 = KeyUtils.exportRawPrivateKey(key2.getPrivateKey());
		assertArrayEquals(raw1, raw2);
	}
	
	@Test
	public void testCreateRawPrivateKeyEncoded() {
		AergoKeyGenerator gen = new AergoKeyGenerator();
		final AergoKey key = gen.create();
		String address = key.getAddress().toString();
		byte[] raw = KeyUtils.exportRawPrivateKey(key.getPrivateKey());
		String rawPrivateKey = Base64Utils.encode(raw);
		String derivedAddress = KeyUtils.deriveAddress(KeyUtils.toECDSAKey(rawPrivateKey));
		assertEquals(address, derivedAddress);
		//System.out.println(address + " // " + rawPrivateKey);
	}
	
}
