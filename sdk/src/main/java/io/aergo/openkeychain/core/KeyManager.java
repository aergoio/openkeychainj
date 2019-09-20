/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

public interface KeyManager {

	public String fetchAddress();
	public Signer getSigner();
	public Verifier getVerifier();
	
}
