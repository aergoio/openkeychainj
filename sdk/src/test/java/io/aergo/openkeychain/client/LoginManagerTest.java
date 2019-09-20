/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.client;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.aergo.openkeychain.client.LoginManager;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.SimpleKeyManager;

public class LoginManagerTest {
	
	@Test
	public void testMockLoginManager() {
		Object[][] testset = {
				{
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
					"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm",
				},
				{
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
					"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP",
				},
		};
		for (Object[] testdata : testset) {
			final KeyManager km = new SimpleKeyManager((String)testdata[0]);
			LoginManager loginManager = new LoginManager(null, km.getSigner());
			assertEquals(testdata[1], loginManager.getAddress());
		}
	}
}
