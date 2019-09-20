/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import java.security.PrivateKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.key.AergoKey;
import hera.util.Base64Utils;
import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.util.KeyUtils;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SimpleKeyManager implements KeyManager {
	
	static final Logger logger = LoggerFactory.getLogger(SimpleKeyManager.class);

	private ECDSAKey key;

	
	public SimpleKeyManager(ECDSAKey key) {
		this.key = key;
	}

	public SimpleKeyManager(PrivateKey key) {
		this(KeyUtils.exportRawPrivateKey(key));
	}

	public SimpleKeyManager(AergoKey key) {
		this(key.getPrivateKey());
	}
	
	public SimpleKeyManager(byte[] rawPrivateKey) {
		this(KeyUtils.toECDSAKey(rawPrivateKey));
	}

	public SimpleKeyManager(String base64EncodedRawPrivateKey) {
		this(Base64Utils.decode(base64EncodedRawPrivateKey));
	}
	
	public SimpleKeyManager(boolean lazyKey) {
		this(lazyKey ? null : KeyUtils.createECDSAKey());
	}
	
	
	public boolean hasKey() {
		return (this.key != null);
	}
	
	private ECDSAKey getKey() {
		if (this.key == null) {
			synchronized (this) {
				if (this.key == null) {
					//logger.debug("create new key ({})", this.toString());
					this.key = KeyUtils.createECDSAKey();
				}
			}
		}
		return this.key;
	}
	
	
	@Override
	public String fetchAddress() {
		return KeyUtils.deriveAddress(getKey());
	}

	@Override
	public Signer getSigner() {
		return new Signer() {
			@Override
			public String getAddress() {
				return fetchAddress();
			}
			@Override
			public byte[] sign(final byte[] hashedMessage) {
				return KeyUtils.signature(getKey(), hashedMessage);
			}
		};
	}

	@Override
	public Verifier getVerifier() {
		return new Verifier() {
			@Override
			public String getAddress() {
				return fetchAddress();
			}
			@Override
			public final boolean verify(byte[] hashedMessage, byte[] signature) {
				return KeyUtils.verify(fetchAddress(), hashedMessage, signature);
			}
		};
	}

}
