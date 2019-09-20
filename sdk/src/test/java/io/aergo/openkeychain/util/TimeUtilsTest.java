/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.aergo.openkeychain.provider.TimestampProvider;

public class TimeUtilsTest {
	
	TimestampProvider tp = TimestampProvider.defaultProvider;
	
	@Test
	public void testWithin() {
		Object[][] testset = {
				{
					"2019-01-23T12:34:56+0900",
					"2019-01-23T12:29:55+0900",
					5, false
				},
				{
					"2019-01-23T12:34:56+0900",
					"2019-01-23T12:29:56+0900",
					5, true
				},
				{
					"2019-01-23T12:34:56+0900",
					"2019-01-23T12:39:56+0900",
					5, true
				},
				{
					"2019-01-23T12:34:56+0900",
					"2019-01-23T12:39:57+0900",
					5, false
				},
		};
		
		for (Object[] testdata : testset) {
			long t1 = tp.asTime((String)testdata[0]);
			long t2 = tp.asTime((String)testdata[1]);
			long minutes = (int)testdata[2];
			boolean expected = (boolean)testdata[3];
			assertEquals(expected, TimeUtils.within(minutes, t1, t2));
		}
	}
	
}
