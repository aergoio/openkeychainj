/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import hera.util.Base64Utils;

public class MetadataImpl extends Metadata.AbstractMetadata implements Metadata {
	
	private byte[] bytes;
	
	@JsonCreator
	public MetadataImpl(final byte[] bytes) {
		this.bytes = bytes;
	}
	
	@JsonValue
	@Override
	public byte[] getBytes() {
		return this.bytes;
	}
	
	
	public static MetadataImpl unmarshal(String source) {
		return new MetadataImpl(source.getBytes(StandardCharsets.UTF_8));
	}
	
	@Override
	public String marshal() {
		return new String(getBytes(), StandardCharsets.UTF_8);
	}
	
	public static MetadataImpl decode(String serialized) {
		return new MetadataImpl(Base64Utils.decode(serialized));
	}

}
