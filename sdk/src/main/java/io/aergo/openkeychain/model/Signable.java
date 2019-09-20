/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import hera.util.Base64Utils;
import hera.util.Sha256Utils;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.core.Verifier;
import io.aergo.openkeychain.util.Jsonizer;
import io.aergo.openkeychain.util.KeyUtils;

public interface Signable {
	
	public String payload();
	
	public String getCertificate();
	public void setCertificate(String certificate);
	public String getSignature();
	public void setSignature(String signature);
	
	public void sign(Signer signer);
	public boolean verify(Verifier verifier);
	public boolean verify();
	
	
	public abstract class AbstractSignable implements Signable {
		
		public void sign(Signer signer) {
			this.setCertificate(signer.getAddress());
			String payload = this.payload();
			byte[] hashedMessage = Sha256Utils.digest(
					payload.getBytes(StandardCharsets.UTF_8));
			byte[] signBytes = signer.sign(hashedMessage);
			String signature = Base64Utils.encode(signBytes);
			this.setSignature(signature);
		}
		
		public boolean verify() {
			return verify(new Verifier() {
				@Override
				public String getAddress() {
					return getCertificate();
				}
				@Override
				public boolean verify(byte[] hashedMessage, byte[] signature) {
					return KeyUtils.verify(getCertificate(), hashedMessage, signature);
				}
				
			});
		}
		
		public boolean verify(Verifier verifier) {
			String payload = this.payload();
			byte[] hashedMessage = Sha256Utils.digest(
					payload.getBytes(StandardCharsets.UTF_8));
			byte[] signature = Base64Utils.decode(this.getSignature());
			return verifier.verify(hashedMessage, signature);
		}
		
		public String marshal() {
			try {
				return Jsonizer.getInstance().getMapper()
						.writerWithView(Signed.class)
						.writeValueAsString(this);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		@Override
		public String toString() {
			return marshal();
		}
		
		@Override
		public String payload() {
			try {
				return Jsonizer.getInstance().getMapper()
						.writerWithView(Signable.class)
						.writeValueAsString(this);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
	
	}
	
	public interface Signed extends Signable {}
	
}
