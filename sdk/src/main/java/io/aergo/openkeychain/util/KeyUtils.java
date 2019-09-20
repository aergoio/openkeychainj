package io.aergo.openkeychain.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.bouncycastle.crypto.InvalidCipherTextException;

import hera.api.model.AccountAddress;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.spec.resolver.AddressResolver;
import hera.spec.resolver.EncryptedPrivateKeyResolver;
import hera.spec.resolver.SignatureResolver;
import hera.util.Base64Utils;
import hera.util.NumberUtils;
import hera.util.pki.ECDSAKey;
import hera.util.pki.ECDSAKeyGenerator;
import hera.util.pki.ECDSASignature;
import hera.util.pki.ECDSAVerifier;

public class KeyUtils {

	public static final String deriveAddress(ECDSAKey ecdsaKey) {
		return AddressResolver.deriveAddress(
				ecdsaKey.getPublicKey()).getEncoded();
	}
	
	public static final String deriveAddress(PublicKey publicKey) {
		return AddressResolver.deriveAddress(
				publicKey).getEncoded();
	}
	
	public static final PublicKey toPublicKey(String accountAddress) {
		return new AccountAddress(accountAddress).asPublicKey();
	}
	
	
	
	public static final ECDSAKey toECDSAKey(AergoKey aergoKey) {
		return ECDSAKey.of(aergoKey.getPrivateKey(),
				aergoKey.getPublicKey(), ECDSAKeyGenerator.ecParams);
	}
	
	public static final ECDSAKey toECDSAKey(byte[] rawPrivateKey) {
		try {
			return new ECDSAKeyGenerator().create(
					new BigInteger(1, rawPrivateKey));
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static final ECDSAKey toECDSAKey(String base64EncodedRawPrivateKey) {
		return toECDSAKey(Base64Utils.decode(base64EncodedRawPrivateKey));
	}
	
	public static final AergoKey toAergoKey(byte[] rawPrivateKey) {
		return new AergoKey(toECDSAKey(rawPrivateKey));
	}
	
	public static final AergoKey toAergoKey(String base64EncodedRawPrivateKey) {
		return toAergoKey(Base64Utils.decode(base64EncodedRawPrivateKey));
	}
	
	
	
	public static final AergoKey createAergoKey() {
		final AergoKey aergoKey = new AergoKeyGenerator().create();
		return aergoKey;
	}
	
	public static final ECDSAKey createECDSAKey() {
		return toECDSAKey(createAergoKey());
	}
	
	public static final byte[] createRawPrivateKey() {
		return exportRawPrivateKey(createAergoKey().getPrivateKey());
	}
	
	public static final String createRawPrivateKeyEncoded() {
		return Base64Utils.encode(createRawPrivateKey());
	}
	
	
	
	public static final byte[] exportRawPrivateKey(PrivateKey privateKey) {
		final org.bouncycastle.jce.interfaces.ECPrivateKey ecPrivateKey =
				(org.bouncycastle.jce.interfaces.ECPrivateKey) privateKey;
		final BigInteger d = ecPrivateKey.getD();
		return NumberUtils.positiveToByteArray(d);
	}
	
	public static final String exportRawPrivateKeyEncoded(PrivateKey privateKey) {
		return Base64Utils.encode(exportRawPrivateKey(privateKey));
	}
	
	public static final byte[] decryptPrivateKeyBytes(
			String b58cEncodedPrivateKey, String password) {
		try {
			final EncryptedPrivateKey encryptedPrivateKey =
					new EncryptedPrivateKey(b58cEncodedPrivateKey);
			final BytesValue decrypted = EncryptedPrivateKeyResolver
					.decrypt(encryptedPrivateKey, password);
			return decrypted.getValue();
		} catch (InvalidCipherTextException | UnsupportedEncodingException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static final byte[] signature(
			final ECDSAKey ecdsaKey, final byte[] hashedMessage) {
		final ECDSASignature ecdsaSignature = ecdsaKey.sign(hashedMessage);
		final Signature signature = SignatureResolver.serialize(
				ecdsaSignature, ecdsaKey.getParams().getN());
		return signature.getSign().getValue();
	}
	
	public static final boolean verify(final String accountAddress,
			final byte[] hashedMessage, final byte[] signature) {
		return verify(toPublicKey(accountAddress), hashedMessage, signature);
	}
	
	public static final boolean verify(final PublicKey publicKey,
			final byte[] hashedMessage, final byte[] signature) {
		final ECDSAVerifier ecdsaVerifier = new ECDSAVerifier(ECDSAKeyGenerator.ecParams);
		final Signature sign = Signature.of(new BytesValue(signature));
		final ECDSASignature parsedSignature =
				SignatureResolver.parse(sign, ecdsaVerifier.getParams().getN());
		return ecdsaVerifier.verify(publicKey, hashedMessage, parsedSignature);
	}
	
}
