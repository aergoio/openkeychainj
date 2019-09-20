/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimestampProviderTest {
	
	@Test
	public void testTime() {
		Object[][] testset = {
				{
					"2019-01-23T12:34:56+0900",
					1_548214_496000L,
				},
				{
					"2019-01-23T12:34:57+0900",
					1_548214_497000L,
				},
				{
					"2019-01-23T12:39:56+0900",
					1_548214_796000L,
				},
		};
		
		TimestampProvider tp = TimestampProvider.defaultProvider;
		for (Object[] testdata : testset) {
			long time = tp.asTime((String)testdata[0]);
			long expected = (long)testdata[1];
			String msg = String.format("%s : %dL", testdata[0], testdata[1]);
			assertEquals(msg, expected, time);
		}
	}
	
	@Test
	public void testTimezone() {
		Object[][] testset = {
				{
					"2019-01-23T03:34:56+0900",
					1_548182_096000L,
				},
				{
					"2019-01-23T03:34:56+0000",
					1_548214_496000L,
				},
				{
					"2019-01-23T12:34:56+0900",
					1_548214_496000L,
				},
				{
					"2019-01-23T12:34:56+0000",
					1_548246_896000L,
				},
		};
		
		TimestampProvider tp = TimestampProvider.defaultProvider;
		for (Object[] testdata : testset) {
			long time = tp.asTime((String)testdata[0]);
			long expected = (long)testdata[1];
			String msg = String.format("%s : %dL", testdata[0], testdata[1]);
			assertEquals(msg, expected, time);
		}
	}
	
	@Test
	public void testMockTimestampProvider() {
		Object[][] testset = {
				{
					"2019-01-23T03:34:56+0900",
					0L, TimeUnit.SECONDS,
					1_548182_096000L,
				},
				{
					"2019-01-23T03:34:56+0000",
					100L, TimeUnit.SECONDS,
					1_548214_596000L,
				},
				{
					"2019-01-23T12:34:56+0900",
					10L, TimeUnit.MINUTES,
					1_548215_096000L,
				},
				{
					"2019-01-23T12:34:56+0000",
					1L, TimeUnit.DAYS,
					1_548333_296000L,
				},
		};
		
		for (Object[] testdata : testset) {
			TimestampProvider tp = new MockTimestampProvider(
					(String)testdata[0], (long)testdata[1], (TimeUnit)testdata[2]);
			assertEquals((long)testdata[3], tp.getTime());
		}
	}
}
