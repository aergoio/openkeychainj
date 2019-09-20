/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataTest {

	static final Logger logger = LoggerFactory.getLogger(MetadataTest.class);
	
	@Test
	public void testMetadataImplEncode() {
		Object[][] testset = {
				{
					"{\"value\":\"text\"}",
					"eyJ2YWx1ZSI6InRleHQifQ==",
				},
				{
					"{\"value\":\"sample\"}",
					"eyJ2YWx1ZSI6InNhbXBsZSJ9",
				},
		};
		for (Object[] testdata : testset) {
			byte[] bytes = ((String)testdata[0]).getBytes(StandardCharsets.UTF_8);
			MetadataImpl data = new MetadataImpl(bytes);
			assertEquals(testdata[0], data.marshal());
			assertEquals(testdata[1], data.encode());
		}
	}
	
	@Test
	public void testMetadataImplDecode() {
		Object[][] testset = {
				{
					"eyJ2YWx1ZSI6InRleHQifQ==",
					"{\"value\":\"text\"}",
				},
				{
					"eyJ2YWx1ZSI6InNhbXBsZSJ9",
					"{\"value\":\"sample\"}",
				},
		};
		for (Object[] testdata : testset) {
			MetadataImpl data = MetadataImpl.decode((String)testdata[0]);
			assertEquals(testdata[1], data.marshal());
		}
	}
	
	@Test
	public void testMetadataImplBind() {
		Object[][] testset = {
				{
					"{\"value\":\"text\"}",
					"text",
					"text",
					null,
				},
				{
					"{\"value\":\"text\",\"test\":\"sample\"}",
					"text",
					"text",
					"sample",
				},
				{
					"{\"test\":\"sample\"}",
					null,
					null,
					"sample",
				},
		};
		for (Object[] testdata : testset) {
			MetadataImpl data = MetadataImpl.unmarshal((String)testdata[0]);
			assertEquals(testdata[1], data.bind(SimpleMetadata.class).getValue());
			assertEquals(testdata[2], data.bind(ExtendedMetadata.class).get("value"));
			assertEquals(testdata[3], data.bind(ExtendedMetadata.class).get("test"));
		}
	}
	
	@Test
	public void testSimpleMetadata() {
		Object[][] testset = {
				{
					"{\"value\":\"sample\"}",
					"{\"value\":\"sample\"}",
					"sample",
				},
				{
					"{\"value\":\"\"}",
					"{\"value\":\"\"}",
					"",
				},
				{
					"{\"value\":null}",
					"{}",
					null,
				},
		};
		for (Object[] testdata : testset) {
			SimpleMetadata data = SimpleMetadata.unmarshal((String)testdata[0]);
			assertEquals(testdata[1], data.marshal());
			assertEquals(testdata[2], data.getValue());
		}
	}
	
	@Test
	public void testExtendedMetadata() {
		Object[][] testset = {
				{
					"{\"key1\":\"value1\",\"key2\":\"value2\"}",
					2,
					new String[] {"key1", "value1"},
					new String[] {"key1", "value1"},
					new String[] {"key2", "value2"},
				},
				{
					"{\"key1\":\"\",\"key2\":null}",
					2,
					new String[] {"key1", ""},
					new String[] {"key2", null},
					new String[] {"key3", null},
				},
				{
					"{}",
					0,
					new String[] {"key", (String)null},
				},
		};
		for (Object[] testdata : testset) {
			ExtendedMetadata data = ExtendedMetadata.unmarshal((String)testdata[0]);
			assertEquals((int)testdata[1], data.getMap().size());
			for (int i=2; i<testdata.length; i++) {
				String[] entry = (String[]) testdata[i];
				assertEquals(entry[1], data.get(entry[0]));
			}
		}
	}
	
	@Test
	public void testSimpleMetadataSorted() {
		Object[][] testset = {
				{
					"{\"bbb\":\"1\",\"aaa\":\"2\",\"ccc\":\"3\"}",
					"{\"aaa\":\"2\",\"bbb\":\"1\",\"ccc\":\"3\"}",
				},
		};
		for (Object[] testdata : testset) {
			ExtendedMetadata data = ExtendedMetadata.unmarshal((String)testdata[0]);
			assertEquals((String)testdata[1], data.marshal());
		}
	}
}
