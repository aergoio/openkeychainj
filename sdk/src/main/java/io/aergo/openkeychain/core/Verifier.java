/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

public interface Verifier {

	public String getAddress();
	public boolean verify(final byte[] hashedMessage, final byte[] signature);

}
