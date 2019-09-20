/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import java.security.PublicKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.api.model.AccountAddress;
import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.util.KeyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerifierImpl implements Verifier {
	
	static final Logger logger = LoggerFactory.getLogger(VerifierImpl.class);
	
	@NonNull
	private final PublicKey publicKey;
	
	
	public VerifierImpl(ECDSAKey key) {
		this(key.getPublicKey());
	}
	
	public VerifierImpl(String accountAddress) {
		this(new AccountAddress(accountAddress).asPublicKey());
	}


	@Override
	public String getAddress() {
		return KeyUtils.deriveAddress(publicKey);
	}
	
	@Override
	public boolean verify(final byte[] hashedMessage, final byte[] signature) {
		return KeyUtils.verify(publicKey, hashedMessage, signature);
	}
	
}
