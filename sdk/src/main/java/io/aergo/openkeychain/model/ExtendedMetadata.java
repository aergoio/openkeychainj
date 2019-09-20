/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.nio.charset.StandardCharsets;
import java.util.SortedMap;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

import hera.util.Base64Utils;
import io.aergo.openkeychain.util.Jsonizer;
import lombok.NonNull;

@JsonPropertyOrder(alphabetic = true)
public class ExtendedMetadata extends Metadata.AbstractMetadata implements Metadata {
	
	@NonNull
	SortedMap<String, Object> map;
	
	
	@JsonCreator
	public ExtendedMetadata(SortedMap<String, Object> map) {
		this.map = map;
	}
	
	public ExtendedMetadata() {
		this(new TreeMap<String, Object>());
	}
	
	
	@JsonValue
	public final SortedMap<String, Object> getMap() {
		return this.map;
	}
	
	public void put(String key, Object value) {
		if (value == null) {
			this.getMap().remove(key);
		}
		else {
			this.getMap().put(key, value);
		}
	}
	
	public Object get(String key) {
		return this.getMap().get(key);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(String key, Class<T> clazz) {
		return (T)this.getMap().get(key);
	}
	
	
	
	public static ExtendedMetadata unmarshal(String source) {
		return Jsonizer.getInstance().read(source, ExtendedMetadata.class);
	}
	
	@Override
	public String marshal() {
		return Jsonizer.getInstance().write(this);
	}
	
	public static ExtendedMetadata decode(String encoded) {
		final byte[] bytes = Base64Utils.decode(encoded);
		return unmarshal(new String(bytes, StandardCharsets.UTF_8));
	}
	
}
