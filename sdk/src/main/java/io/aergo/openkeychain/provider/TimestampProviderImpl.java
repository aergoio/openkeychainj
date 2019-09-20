/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.Getter;
import lombok.NonNull;

public class TimestampProviderImpl implements TimestampProvider {

	@NonNull @Getter
	final DateFormat formatter;
	
	@NonNull @Getter
	final String pattern;
	
	
	public TimestampProviderImpl() {
		this(TimestampProvider.defaultPattern);
	}
	
	public TimestampProviderImpl(final String pattern) {
		this.formatter = new SimpleDateFormat(pattern);
		this.pattern = pattern;
	}
	
	@Override
	public String getTimestamp() {
		return formatter.format(this.getTime());
	}
	
	@Override
	public String getTimestamp(long time) {
		return formatter.format(time);
	}
	
	@Override
	public long getTime() {
		return System.currentTimeMillis();
	}
	
	@Override
	public long asTime(String timestamp) {
		try {
			return formatter.parse(timestamp).getTime();
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
	@Override
	public Date asDate(String timestamp) {
		try {
			return formatter.parse(timestamp);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e);
		}
	}
	
}
