/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

public interface Signer {
	
	public String getAddress();
	public byte[] sign(final byte[] hashedMessage);
	
}
