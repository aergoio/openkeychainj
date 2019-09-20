/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.aergo.openkeychain.core.ChallengeResponseClient;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.Publishers;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Response;
import io.aergo.openkeychain.provider.ContextProvider;
import io.aergo.openkeychain.provider.MockTimestampProvider;
import io.aergo.openkeychain.provider.TimestampProvider;

public class ChallengeResponseTest {
	
	static final Logger logger = LoggerFactory.getLogger(ChallengeResponseTest.class);
	
	
	@Test
	public void testCreateChallenge() {
		Object[][] testset = {
				{
					"mwEbBvfwVvS4RWUqRhdMo4FjTHtYMOpmwStkDagY1og=",
					"2019-07-01T12:34:56+0900",
					"sample",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
				},
				{
					"iberiE82XHC3/vKCbUzoBQTrhdctdZqzicmjIrzLokY=",
					"2019-07-01T12:34:56+0900",
					"sample",
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
				},
		};
		for (Object[] testdata : testset) {
			final KeyManager keyManager = new SimpleKeyManager((String)testdata[0]);
			final String timestamp = (String)testdata[1];
			final String context = (String)testdata[2];
			ChallengeResponseServer server = new ChallengeResponseServer() {
				@Override
				public Publishers getPublishers() {
					return null;
				}
				@Override
				public Signer getSigner() {
					return keyManager.getSigner();
				}
				@Override
				public ContextProvider getContextProvider() {
					return new ContextProvider() {
						@Override
						public String getContext() {
							return context;
						}
					};
				}
				@Override
				public TimestampProvider getTimestampProvider() {
					return new MockTimestampProvider(timestamp);
				}
			};
			assertEquals(testdata[3], server.createChallenge().marshal());
		}
	}
	
	@Test
	public void testCheckChallenge() {
		Object[][] testset = {
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
					true,
				},
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
					true,
				},
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
					false,
				},
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmQ2WTZG16r44MAS8gQdkANYV7aMXENLHsmqFYdMLyLCVa7HzRrB\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
					false,
				},
		};
		for (Object[] testdata : testset) {
			final String[] serverAddresses = (String[])testdata[0];
			final Challenge challenge = Challenge.unmarshal((String)testdata[1]);
			ChallengeResponseClient client = new ChallengeResponseClient() {
				Publishers publishers = new SimplePublishers(serverAddresses);
				@Override
				public Publishers getPublishers() {
					return publishers;
				}
				@Override
				public Signer getSigner() {
					return null;
				}
				@Override
				public TimestampProvider getTimestampProvider() {
					return new MockTimestampProvider(
							challenge.getTimestamp(), 10L, TimeUnit.SECONDS);
				}
			};
			assertEquals((boolean)testdata[2], client.checkChallenge(challenge));
		}
	}
	
	@Test
	public void testCreateResponse() {
		Object[][] testset = {
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm\",\"signature\":\"MEUCIQCgbKL19GMkc8VksfcPEuBh2fgWzcgCvOS0H8SkOcduzAIgeU+norA+LUkJ4Q0W5/pA5rkPDYzN2Hsm2Cy6twzySlk=\"}",
					"QjOYktzZ+7EL7q/k2IqTBFay1ra539ttpS1GS6xHfI4=",
					"{\"timestamp\":\"2019-07-01T12:35:06+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJBbU5mSzk0c3dad1ZOeVkxb0czQWpCSDRZb2lTWXFRR0NNb1JYQzhDajU5SzRobkhmNmNtIiwic2lnbmF0dXJlIjoiTUVVQ0lRQ2diS0wxOUdNa2M4VmtzZmNQRXVCaDJmZ1d6Y2dDdk9TMEg4U2tPY2R1ekFJZ2VVK25vckErTFVrSjRRMFc1L3BBNXJrUERZek4ySHNtMkN5NnR3enlTbGs9In0=\",\"certificate\":\"AmQ2WTZG16r44MAS8gQdkANYV7aMXENLHsmqFYdMLyLCVa7HzRrB\",\"signature\":\"MEQCIHCVl7499lkjKrtfWhHaKsv5zMBGWewCdCLAF8GEWu34AiBcIAh2ibe54LCkKrnnt5XdI2CrZmgoYEhETlwDhZSVMg==\"}",
				},
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:34:56+0900\",\"context\":\"sample\",\"certificate\":\"AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP\",\"signature\":\"MEMCICLEtERm8efMsh5vvA0jknkKq/LIAhmlQR56BXTtfxxwAh8eT2PQuimOk/PPDvIsw+IQMa9gWJG/RKRFMpSXQBXm\"}",
					"QjOYktzZ+7EL7q/k2IqTBFay1ra539ttpS1GS6xHfI4=",
					"{\"timestamp\":\"2019-07-01T12:35:06+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJBbU5aclRkRExlWHBnWXI3SHFSV0R4TFUxZG5mYWVXRmlvNEQ2OTZZZnJoaG1VNDhKQm9QIiwic2lnbmF0dXJlIjoiTUVNQ0lDTEV0RVJtOGVmTXNoNXZ2QTBqa25rS3EvTElBaG1sUVI1NkJYVHRmeHh3QWg4ZVQyUFF1aW1Pay9QUER2SXN3K0lRTWE5Z1dKRy9SS1JGTXBTWFFCWG0ifQ==\",\"certificate\":\"AmQ2WTZG16r44MAS8gQdkANYV7aMXENLHsmqFYdMLyLCVa7HzRrB\",\"signature\":\"MEUCIQDXCUGkPWUZH8xis5N/ocJ7ZWx+jveOl88ojDN2nNerUQIgdrCOYXYyrigfSF0HMMAWSq48xuBRJA3IyujSjMlfk2c=\"}",
				},
		};
		for (Object[] testdata : testset) {
			final String[] serverAddresses = (String[])testdata[0];
			final Challenge challenge = Challenge.unmarshal((String)testdata[1]);
			final KeyManager keyManager = new SimpleKeyManager((String)testdata[2]);
			
			ChallengeResponseClient client = new ChallengeResponseClient() {
				Publishers publishers = new SimplePublishers(serverAddresses);
				@Override
				public Publishers getPublishers() {
					return publishers;
				}
				@Override
				public Signer getSigner() {
					return keyManager.getSigner();
				}
				@Override
				public TimestampProvider getTimestampProvider() {
					return new MockTimestampProvider(
							challenge.getTimestamp(), 10L, TimeUnit.SECONDS);
				}
			};
			assertTrue(client.checkChallenge(challenge));
			Response response = client.createResponse(challenge);
			assertEquals(testdata[3], response.marshal());
		}
	}
	
	@Test
	public void testCheckResponse() {
		Object[][] testset = {
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:35:06+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJBbU5mSzk0c3dad1ZOeVkxb0czQWpCSDRZb2lTWXFRR0NNb1JYQzhDajU5SzRobkhmNmNtIiwic2lnbmF0dXJlIjoiTUVVQ0lRQ2diS0wxOUdNa2M4VmtzZmNQRXVCaDJmZ1d6Y2dDdk9TMEg4U2tPY2R1ekFJZ2VVK25vckErTFVrSjRRMFc1L3BBNXJrUERZek4ySHNtMkN5NnR3enlTbGs9In0=\",\"certificate\":\"AmQ2WTZG16r44MAS8gQdkANYV7aMXENLHsmqFYdMLyLCVa7HzRrB\",\"signature\":\"MEQCIHCVl7499lkjKrtfWhHaKsv5zMBGWewCdCLAF8GEWu34AiBcIAh2ibe54LCkKrnnt5XdI2CrZmgoYEhETlwDhZSVMg==\"}",
					true,
					true, "sample",
					false, "invalid context",
				},
				{
					new String[] {"AmNfK94swZwVNyY1oG3AjBH4YoiSYqQGCMoRXC8Cj59K4hnHf6cm", "AmNZrTdDLeXpgYr7HqRWDxLU1dnfaeWFio4D696YfrhhmU48JBoP"},
					"{\"timestamp\":\"2019-07-01T12:35:06+0900\",\"context\":\"eyJ0aW1lc3RhbXAiOiIyMDE5LTA3LTAxVDEyOjM0OjU2KzA5MDAiLCJjb250ZXh0Ijoic2FtcGxlIiwiY2VydGlmaWNhdGUiOiJBbU5aclRkRExlWHBnWXI3SHFSV0R4TFUxZG5mYWVXRmlvNEQ2OTZZZnJoaG1VNDhKQm9QIiwic2lnbmF0dXJlIjoiTUVNQ0lDTEV0RVJtOGVmTXNoNXZ2QTBqa25rS3EvTElBaG1sUVI1NkJYVHRmeHh3QWg4ZVQyUFF1aW1Pay9QUER2SXN3K0lRTWE5Z1dKRy9SS1JGTXBTWFFCWG0ifQ==\",\"certificate\":\"AmQ2WTZG16r44MAS8gQdkANYV7aMXENLHsmqFYdMLyLCVa7HzRrB\",\"signature\":\"MEUCIQDXCUGkPWUZH8xis5N/ocJ7ZWx+jveOl88ojDN2nNerUQIgdrCOYXYyrigfSF0HMMAWSq48xuBRJA3IyujSjMlfk2c=\"}",
					true,
					true, "sample",
					false, "invalid context",
				},
		};
		for (Object[] testdata : testset) {
			final String[] serverAddresses = (String[])testdata[0];
			final Response response = Response.unmarshal((String)testdata[1]);
			
			ChallengeResponseServer server = new ChallengeResponseServer() {
				Publishers publishers = new SimplePublishers(serverAddresses);
				@Override
				public Publishers getPublishers() {
					return publishers;
				}
				@Override
				public Signer getSigner() {
					return null;
				}
				@Override
				public TimestampProvider getTimestampProvider() {
					return new MockTimestampProvider(
							response.getChallenge().getTimestamp(), 10L, TimeUnit.SECONDS);
				}
			};
			assertEquals((String)testdata[1], response.marshal());
			assertEquals((boolean)testdata[2], server.checkResponse(response));
			assertEquals((boolean)testdata[2], server.checkResponse(response, null));
			assertEquals((boolean)testdata[3], server.checkResponse(response, (String)testdata[4]));
			assertEquals((boolean)testdata[5], server.checkResponse(response, (String)testdata[6]));
		}
	}
}
