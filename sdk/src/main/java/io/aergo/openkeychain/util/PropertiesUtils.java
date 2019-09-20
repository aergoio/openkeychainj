/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import hera.util.IoUtils;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;

public class PropertiesUtils {
	
	public static final String XML_SUFFIX = ".xml";
	
	
	public static Properties init(final Class<?> clazz, final String resourceName) throws IOException {
		try {
			URL url = clazz.getResource(resourceName);
			if (url == null) {
				return new Properties();
			}
			return init(new File(url.toURI()));
		} catch (URISyntaxException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	public static Properties init(final File source) throws IOException {
		if (!source.exists()) {
			return new Properties();
		}
		@Cleanup InputStream in = new FileInputStream(source);
		boolean isXml = source.getName().endsWith(XML_SUFFIX);
		return init(in, isXml);
	}
	
	public static Properties init(final InputStream in, final boolean isXml) throws IOException {
		byte[] sourceBytes = IoUtils.from(in);
		if (sourceBytes == null || sourceBytes.length == 0) {
			return new Properties();
		}
		String sourceStr = new String(sourceBytes, StandardCharsets.UTF_8);
		if (sourceStr.trim().isEmpty()) {
			return new Properties();
		}
		@Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(sourceBytes);
		return load(bis, isXml);
	}
	
	
	/*
	public static Properties store(final Properties properties, final Class<?> clazz, final String resourceName
			) throws IOException {
		String path = null;
		URL url = clazz.getResource(resourceName);
		if (url == null) {
			url = clazz.getClassLoader().getResource("");
			path = url.toString() + resourceName;
		}
		return store(properties, new File(path));
	}
	*/
	
	public static Properties store(final Properties properties, final File source) throws IOException {
		if (!source.exists()) {
			if (!source.getParentFile().exists()) {
				source.getParentFile().mkdirs();
			}
			source.createNewFile();
		}
		@Cleanup OutputStream out = new FileOutputStream(source);
		boolean isXml = source.getName().endsWith(XML_SUFFIX);
		return store(properties, out, isXml);
	}
	
	public static Properties store(final Properties properties, final OutputStream out, final boolean isXml) throws IOException {
		if (isXml) {
			properties.storeToXML(out, null, StandardCharsets.UTF_8.name());
		}
		else {
			properties.store(out, null);
		}
		return properties;
	}
	
	
	public static Properties load(final Class<?> clazz, final String resourceName) throws IOException {
		@Cleanup InputStream in = clazz.getResourceAsStream(resourceName);
		boolean isXml = resourceName.endsWith(XML_SUFFIX);
		return load(in, isXml);
	}
	
	public static Properties load(final File source) throws IOException {
		@Cleanup InputStream in = new FileInputStream(source);
		boolean isXml = source.getName().endsWith(XML_SUFFIX);
		return load(in, isXml);
	}
	
	public static Properties load(final InputStream in, final boolean isXml) throws IOException {
		Properties props = new Properties();
		if (isXml) {
			props.loadFromXML(in);
		}
		else {
			props.load(in);
		}
		return props;
	}
	
	
	public static class PropertiesFile {
		
		@NonNull @Getter
		private final Properties properties;
		
		@Getter
		private final File source;
		
		public PropertiesFile(Properties properties) {
			this.source = null;
			this.properties = properties;
		}
		
		public PropertiesFile(File source) {
			this.source = source;
			try {
				this.properties = PropertiesUtils.init(source);
			} catch (IOException e) {
				throw new IllegalArgumentException(e);
			}
		}
		
		public void store() {
			if (this.getSource() != null) {
				try {
					PropertiesUtils.store(this.getProperties(), this.getSource());
				} catch (IOException e) {
					throw new IllegalArgumentException(e);
				}
			}
		}
		
		public String getProperty(String key) {
			return this.getProperties().getProperty(key);
		}
		public String getProperty(String key, String defaultValue) {
			return this.getProperties().getProperty(key, defaultValue);
		}
		public Object get(String key) {
			return this.getProperties().get(key);
		}
		public Object setProperty(String key, String value) {
			return this.getProperties().setProperty(key, value);
		}
		
	}
	
}
