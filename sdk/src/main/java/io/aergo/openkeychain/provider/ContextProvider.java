/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.provider;

import java.security.SecureRandom;

public interface ContextProvider {

	public String getContext();
	
	
	ContextProvider defaultProvider = new ContextProvider() {
		private final SecureRandom random = new SecureRandom();
		
		@Override
		public String getContext() {
			long value = random.nextLong();
			return String.valueOf((value < 0) ? (value * -1) : value);
		}
	};
}
