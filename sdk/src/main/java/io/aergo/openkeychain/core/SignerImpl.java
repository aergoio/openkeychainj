/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import hera.key.AergoKey;
import hera.util.Base64Utils;
import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.util.KeyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignerImpl implements Signer {
	
	@NonNull
	private final ECDSAKey ecdsaKey;
	
	
	public SignerImpl(AergoKey aergoKey) {
		this(KeyUtils.toECDSAKey(aergoKey));
	}
	
	public SignerImpl(byte[] rawPrivateKey) {
		this(KeyUtils.toECDSAKey(rawPrivateKey));
	}
	
	public SignerImpl(String base64EncodedRawPrivateKey) {
		this(Base64Utils.decode(base64EncodedRawPrivateKey));
	}
	
	
	@Override
	public String getAddress() {
		return KeyUtils.deriveAddress(ecdsaKey);
	}
	
	@Override
	public byte[] sign(final byte[] hashedMessage) {
		return KeyUtils.signature(ecdsaKey, hashedMessage);
	}
	
}
