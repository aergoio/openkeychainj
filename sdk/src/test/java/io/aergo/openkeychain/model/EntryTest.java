/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntryTest {
	
	static final Logger logger = LoggerFactory.getLogger(EntryTest.class);
	
	@Test
	public void testSerialize() {
		Object[][] testset = {
				{
					new Entry(),
					"{}",
					new Object[] {false},
				},
				{
					Entry.of("sample"),
					"{\"addr\":\"sample\"}",
					new Object[] {false},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text")),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "pubs", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "pubs", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true,\"publisher\":\"pubs\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "pubs", "sign"),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\",\"signature\":\"sign\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "pubs", "sign"),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true,\"publisher\":\"pubs\",\"signature\":\"sign\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
		};
		for (Object[] testdata : testset) {
			Entry entry = (Entry)testdata[0];
			assertEquals((String)testdata[1], entry.marshal());
			Object[] datas = (Object[]) testdata[2];
			if ((boolean)datas[0]) {
				assertEquals((String)datas[1], entry.getData().bind(SimpleMetadata.class).marshal());
				assertEquals((String)datas[1], entry.getData().bind(ExtendedMetadata.class).marshal());
			}
		}
	}
	
	@Test
	public void testDeserialize() {
		Object[][] testset = {
				{
					"{}",
					null,
					null,
					null,
					false,
				},
				{
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					"sample",
					"text",
					null,
					false,
				},
				{
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					"sample",
					"text",
					null,
					false,
				},
				{
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true}",
					"sample",
					"text",
					null,
					true,
				},
				{
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\"}",
					"sample",
					"text",
					"pubs",
					false,
				},
				{
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\",\"revoked\":true}",
					"sample",
					"text",
					"pubs",
					true,
				},
		};
		for (Object[] testdata : testset) {
			Entry data = Entry.unmarshal((String)testdata[0]);
			assertEquals((String)testdata[1], data.getAddr());
			assertEquals((String)testdata[3], data.getCertificate());
			assertEquals((boolean)testdata[4], data.isRevoked());
			if (data.getData() != null) {
				assertEquals((String)testdata[2], data.getData().bind(SimpleMetadata.class).getValue());
				assertEquals((String)testdata[2], data.getData().bind(ExtendedMetadata.class).get("value"));
			}
		}
	}

	@Test
	public void testSignAndVerify() {
		Object[][] testset = {
				{
					new Entry(),
					"{}",
					new Object[] {false},
				},
				{
					Entry.of("sample"),
					"{\"addr\":\"sample\"}",
					new Object[] {false},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text")),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "pubs", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "pubs", null),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true,\"publisher\":\"pubs\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), false, "pubs", "sign"),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"publisher\":\"pubs\",\"signature\":\"sign\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
				{
					Entry.of("sample", SimpleMetadata.of("text"), true, "pubs", "sign"),
					"{\"addr\":\"sample\",\"data\":\"eyJ2YWx1ZSI6InRleHQifQ==\",\"revoked\":true,\"publisher\":\"pubs\",\"signature\":\"sign\"}",
					new Object[] {true, "{\"value\":\"text\"}"},
				},
		};
		for (Object[] testdata : testset) {
			Entry entry = (Entry)testdata[0];
			assertEquals((String)testdata[1], entry.marshal());
			Object[] datas = (Object[]) testdata[2];
			if ((boolean)datas[0]) {
				assertEquals((String)datas[1], entry.getData().bind(SimpleMetadata.class).marshal());
				assertEquals((String)datas[1], entry.getData().bind(ExtendedMetadata.class).marshal());
			}
		}
	}
	
}

