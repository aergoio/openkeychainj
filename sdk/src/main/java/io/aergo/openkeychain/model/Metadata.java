/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonIgnore;

import hera.util.Base64Utils;
import io.aergo.openkeychain.util.Jsonizer;

public interface Metadata {
	
	public String marshal();
	
	public byte[] getBytes();
	
	public String encode();
	
	public <T extends Metadata> T bind(Class<T> clazz);
	
	
	
	public abstract class AbstractMetadata implements Metadata {
		
		@JsonIgnore
		@Override
		public byte[] getBytes() {
			return marshal().getBytes(StandardCharsets.UTF_8);
		}
		
		@Override
		public String toString() {
			return marshal();
		}
		
		@Override
		public String encode() {
			return Base64Utils.encode(this.getBytes());
		}
		
		@Override
		public <T extends Metadata> T bind(Class<T> clazz) {
			return Jsonizer.getInstance().read(
					new ByteArrayInputStream(this.getBytes()), clazz);
		}
		
	}
	
}
