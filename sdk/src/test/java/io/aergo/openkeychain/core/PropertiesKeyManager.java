/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import java.io.File;
import java.util.Properties;

import hera.util.Base64Utils;
import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.util.KeyUtils;
import io.aergo.openkeychain.util.PropertiesUtils;

public class PropertiesKeyManager extends PropertiesUtils.PropertiesFile implements KeyManager {
	
	
	public static final String PRIVATEKEY = "privatekey";
	public static final String ADDRESS = "address";
	
	
	public PropertiesKeyManager(Properties properties) {
		super(properties);
	}
	
	public PropertiesKeyManager(File source) {
		super(source);
	}
	
	
	private ECDSAKey createNewPrivateKey() {
		final ECDSAKey key = KeyUtils.createECDSAKey();
		final byte[] rawPrivateKey = KeyUtils.exportRawPrivateKey(key.getPrivateKey());
		final String accountAddress = KeyUtils.deriveAddress(key.getPublicKey());
		this.setProperty(PRIVATEKEY, Base64Utils.encode(rawPrivateKey));
		this.setProperty(ADDRESS, accountAddress);
		this.store();
		return key;
	}
	
	
	private final ECDSAKey loadPrivateKey() {
		String rawPrivateKey = this.getProperty(PRIVATEKEY, null);
		if (rawPrivateKey != null && !rawPrivateKey.isEmpty()) {
			return KeyUtils.toECDSAKey(rawPrivateKey);
		}
		return createNewPrivateKey();
	}
	
	
	@Override
	public String fetchAddress() {
		String addr = this.getProperty(ADDRESS, null);
		if (addr != null && !addr.isEmpty()) {
			return addr;
		}
		String rawPrivateKey = this.getProperty(PRIVATEKEY, null);
		if (rawPrivateKey != null && !rawPrivateKey.isEmpty()) {
			// derive from private key
			addr = KeyUtils.deriveAddress(KeyUtils.toECDSAKey(rawPrivateKey));
			// save derived address
			this.setProperty(ADDRESS, addr);
			this.store();
			return addr;
		}
		// create new
		ECDSAKey ecdsaKey = createNewPrivateKey();
		return KeyUtils.deriveAddress(ecdsaKey);
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
				return KeyUtils.signature(loadPrivateKey(), hashedMessage);
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
			public boolean verify(byte[] hashedMessage, byte[] signature) {
				return KeyUtils.verify(fetchAddress(), hashedMessage, signature);
			}
		};
	}

}
