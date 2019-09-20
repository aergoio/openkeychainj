/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.core.SignerImpl;
import io.aergo.openkeychain.core.VerifierImpl;
import io.aergo.openkeychain.util.KeyUtils;

public class ResponseTest {

	static final Logger logger = LoggerFactory.getLogger(ResponseTest.class);
	
	@Test
	public void testMarshalAndPayload() {
		Object[][] testset = {
				{
					new Response("2019-07-01T13:04:55+0900", new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign")),
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\"}",
				},
				{
					new Response("2019-07-01T13:04:55+0900", new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign"), "sampleCertificate", "sampleSignature"),
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"sampleCertificate\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"sampleCertificate\",\"signature\":\"sampleSignature\"}",
				},
		};
		for (Object[] testdata : testset) {
			Response response = (Response)testdata[0];
			assertEquals(testdata[1], response.payload());
			assertEquals(testdata[2], response.marshal());
		}
	}
	
	@Test
	public void testUnmarshal() {
		Object[][] testset = {
				{
					"{\"certificate\":\"sampleCertificate\",\"signature\":\"sampleSignature\",\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\"}",
					"sampleCertificate",
					"sampleSignature",
					"2019-07-01T13:04:55+0900",
					"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\",\"signature\":\"sign\"}",
				},
		};
		for (Object[] testdata : testset) {
			Response response = Response.unmarshal((String)testdata[0]);
			assertEquals(testdata[1], response.getCertificate());
			assertEquals(testdata[2], response.getSignature());
			assertEquals(testdata[3], response.getTimestamp());
			assertEquals(testdata[4], response.getContext());
			assertEquals(testdata[5], response.getChallenge().marshal());
		}
	}
	
	@Test
	public void testSign() {
		Object[][] testset = {
				{
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
					new Response("2019-07-01T13:04:55+0900", new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign")),
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCCSIgfZYFqwjlGWD0yvYxPA1VOo/A2RJcwdvmEpw8hMAIgfk6Mogto0dK4Iqv+Zl5brGtsiY2XO14dGc6PP5yUCWs=\"}",
				},
				{
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
					new Response("2019-07-01T13:04:55+0900", new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign")),
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEQCIHa2vmX9UVRKVFc0ehWZxwHFKPeWJeg/9r90hAwHaITYAiBRmqy7ty8bczKCfgl8PTzZfeKQs2c55/zZuEHc4c/cDQ==\"}",
				},
		};
		for (Object[] testdata : testset) {
			Signer signer = new SignerImpl((String)testdata[0]);
			Response response = (Response)testdata[1];
			response.sign(signer);
			assertEquals(testdata[2], response.payload());
			assertEquals(testdata[3], response.marshal());
		}
	}
	
	@Test
	public void testVerify() {
		Object[][] testset = {
				{
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwic2lnbmF0dXJlIjoic2lnbiJ9\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEQCIDsaWeYu3kAsGltTjGanry0RUtMxTPDGxTj66XvuUf73AiBPtTOUvxV2ZeyDUPaxEDc9/MH4WnZ9dMLZAXzxeYmcvg==\"}",
				},	
				{
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwic2lnbmF0dXJlIjoic2lnbiJ9\"}",
					"{\"timestamp\":\"2019-07-01T13:04:55+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwic2lnbmF0dXJlIjoic2lnbiJ9\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEUCIQC11OWc0whUydnCA4TuKHrZ9hovgkKkf0dss5S/Eh0mUQIgeJCpF290LnAecIinuP81hS8QGNplEeHADHZTnAfbq1E=\"}",
				},	
		};
		for (Object[] testdata : testset) {
			ECDSAKey key = KeyUtils.toECDSAKey((String)testdata[0]);
			Response response = Response.unmarshal((String)testdata[1]);
			response.sign(new SignerImpl(key));
			assertEquals(testdata[2], response.marshal());
			
			assertTrue(response.verify());
			assertTrue(response.verify(new VerifierImpl(key)));
		}
	}
	
}
