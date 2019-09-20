/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import java.util.Date;

public interface TimestampProvider {
	
	public String getPattern();
	public String getTimestamp();
	public String getTimestamp(long time);
	public long getTime();
	public long asTime(String timestamp);
	public Date asDate(String timestamp);
	
	
	final String defaultPattern = "yyyy-MM-dd'T'HH:mm:ssZZ";
	final TimestampProvider defaultProvider = new TimestampProviderImpl();
}
