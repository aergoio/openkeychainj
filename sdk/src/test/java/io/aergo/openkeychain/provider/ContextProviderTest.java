/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextProviderTest {

	static final Logger logger = LoggerFactory.getLogger(ContextProviderTest.class);
	
	@Test
	public void testContextProvider() {
		Object[][] testset = {
				{ 1_000L, },
				{ 10_000L, },
				{ 100_000L, },
				{ 1_000_000L, },
		};
		ContextProvider cp = ContextProvider.defaultProvider;
		for (Object[] testdata : testset) {
			long count = (long)testdata[0];
			long time = testContextRepeat(count, cp);
			//logger.debug("{}", String.format("%8dms    %8dns/req  *  %8d req", time/1000, (time/count), count));
			assertTrue(time > 0);
		}
	}
	
	public long testContextRepeat(long count, ContextProvider cp) {
		long start = System.nanoTime();
		for (long i=0; i<count; i++) {
			cp.getContext();
		}
		long end = System.nanoTime();
		return end - start;
	}
}
