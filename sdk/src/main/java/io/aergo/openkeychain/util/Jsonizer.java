/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.Getter;
import lombok.NonNull;

public class Jsonizer {

	private static Jsonizer _instance = null;

	public static final Jsonizer getInstance() {
		if (_instance == null) {
			synchronized (Jsonizer.class) {
				if (_instance == null) {
					_instance = new Jsonizer();
				}
			}
		}
		return _instance;
	}

	@NonNull @Getter
	final ObjectMapper mapper;
	
	
	private Jsonizer() {
		this.mapper = new ObjectMapper();
		this.mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
	}
	
	
	public static String writejs(Object value) {
		return getInstance().write(value);
	}
	
	public String write(Object value) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		write(out, value);
		return new String(out.toByteArray(), StandardCharsets.UTF_8);
	}
	
	public void write(OutputStream out, Object value) {
		try {
			this.getMapper().writeValue(out, value);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	
	public static <T> T readjs(String src, Class<T> clazz) {
		return getInstance().read(src, clazz);
	}
	
	public <T> T read(String src, Class<T> clazz) {
		return read(new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)), clazz);
	}
	
	public <T> T read(InputStream in, Class<T> clazz) {
		try {
			return mapper.reader().forType(clazz).readValue(in);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static <T> T readjs(String src, TypeReference<T> typeRef) {
		return getInstance().read(src, typeRef);
	}
	
	public <T> T read(String src, TypeReference<T> typeRef) {
		return read(new ByteArrayInputStream(src.getBytes(StandardCharsets.UTF_8)), typeRef);
	}
	
	public <T> T read(InputStream in, TypeReference<T> typeRef) {
		try {
			return mapper.reader().forType(typeRef).readValue(in);
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}
}
