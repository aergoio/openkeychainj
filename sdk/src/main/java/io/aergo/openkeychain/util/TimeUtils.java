/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.util;

import java.util.concurrent.TimeUnit;

import io.aergo.openkeychain.provider.TimestampProvider;

public class TimeUtils {

	
	public static boolean within(long minutes, long time1, long time2) {
		if (minutes <= 0) {
			return true;
		}
		long millis = TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);
		long diff = Math.abs(time1 - time2);
		return millis >= diff;
	}
	
	public static boolean within(long minutes, TimestampProvider timestampProvider, String timestamp) {
		if (minutes <= 0) {
			return true;
		}
		long now = timestampProvider.getTime();
		long challengeTime = timestampProvider.asTime(timestamp);
		return within(minutes, now, challengeTime);
	}
	
}
