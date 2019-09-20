/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.util.pki.ECDSAKey;
import io.aergo.openkeychain.core.SignerImpl;
import io.aergo.openkeychain.core.VerifierImpl;
import io.aergo.openkeychain.util.KeyUtils;

public class ChallengeTest {
	
	static final Logger logger = LoggerFactory.getLogger(ChallengeTest.class);
	
	@Test
	public void testUnmarshal() {
		Object[][] testset = {
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
					"sample", "2019-07-01T12:34:56+0900", null, null
				},
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\",\"signature\":\"sign\"}",
					"sample", "2019-07-01T12:34:56+0900", "address", "sign"
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = Challenge.unmarshal((String)testdata[0]);
			assertEquals(testdata[1], challenge.getContext());
			assertEquals(testdata[2], challenge.getTimestamp());
			assertEquals(testdata[3], challenge.getCertificate());
			assertEquals(testdata[4], challenge.getSignature());
		}
	}
	
	@Test
	public void testMarshal() {
		Object[][] testset = {
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample"),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}"
				},
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign"),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\",\"signature\":\"sign\"}"
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = (Challenge)testdata[0];
			assertEquals(testdata[1], challenge.marshal());
		}
	}
	
	@Test
	public void testPayloadSignature() {
		Object[][] testset = {
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample"),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
				},
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample", "address", null),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\"}",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\"}",
				},
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign"),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\"}",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\",\"signature\":\"sign\"}",
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = (Challenge)testdata[0];
			assertEquals(testdata[1], challenge.payload());
			assertEquals(testdata[2], challenge.marshal());
		}
	}
	
	@Test
	public void testSetAddressAndSignature() {
		Object[][] testset = {
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample"),
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
					"address",
					"sign",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"address\",\"signature\":\"sign\"}",
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = (Challenge)testdata[0];
			assertEquals(testdata[1], challenge.marshal());
			challenge.setCertificate((String)testdata[2]);
			challenge.setSignature((String)testdata[3]);
			assertEquals(testdata[4], challenge.marshal());
		}
	}
	
	@Test
	public void testSign() {
		Object[][] testset = {
				{
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
				},
				{
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\"}",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
				},
		};
		for (Object[] testdata : testset) {
			ECDSAKey key = KeyUtils.toECDSAKey((String)testdata[0]);
			Challenge challenge = Challenge.unmarshal((String)testdata[1]);
			challenge.sign(new SignerImpl(key));
			assertEquals(testdata[2], challenge.marshal());
		}
	}

	@Test
	public void testVerify() {
		Object[][] testset = {
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
					true,
					"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm",
					true,
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
				},
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
					true,
					"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm",
					false,
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
				},
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
					true,
					"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP",
					false,
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
				},
				{
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
					true,
					"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP",
					true,
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = Challenge.unmarshal((String)testdata[0]);
			assertEquals((boolean)testdata[1], challenge.verify());
			assertEquals((boolean)testdata[3], challenge.verify(new VerifierImpl((String)testdata[2])));
		}
	}
	
	@Test
	public void testEncode() {
		Object[][] testset = {
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample"),
					"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIn0=",
				},
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample", "address", null),
					"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIn0=",
				},
				{
					new Challenge("2019-07-01T12:34:56+0900", "sample", "address", "sign"),
					"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJhZGRyZXNzIiwic2lnbmF0dXJlIjoic2lnbiJ9",
				},
		};
		for (Object[] testdata : testset) {
			Challenge challenge = (Challenge)testdata[0];
			String encoded = challenge.encode();
			assertEquals((String)testdata[1], encoded);
			Challenge decoded = Challenge.decode(encoded);
			assertEquals(challenge.marshal(), decoded.marshal());
		}
	}
}
