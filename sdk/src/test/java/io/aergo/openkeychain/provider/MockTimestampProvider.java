/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import java.util.concurrent.TimeUnit;

public class MockTimestampProvider extends TimestampProviderImpl {
	long time;
	
	public MockTimestampProvider(String timestamp) {
		this(timestamp, 0L);
	}
	
	public MockTimestampProvider(String timestamp, long shiftDuration, TimeUnit shiftUnit) {
		this(timestamp, TimeUnit.MILLISECONDS.convert(shiftDuration, shiftUnit));
	}
	
	public MockTimestampProvider(String timestamp, long timeShift) {
		this.time = this.asTime(timestamp) + timeShift;
	}
	
	@Override
	public long getTime() {
		return this.time;
	}
	
}
