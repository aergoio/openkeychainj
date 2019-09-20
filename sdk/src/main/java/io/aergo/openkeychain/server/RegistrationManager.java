/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.server;

import java.io.IOException;

import io.aergo.openkeychain.backend.Backend;
import io.aergo.openkeychain.core.ChallengeResponseServer;
import io.aergo.openkeychain.core.Publishers;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.core.SimplePublishers;
import io.aergo.openkeychain.model.Entry;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class RegistrationManager extends ChallengeResponseServer {
	
	@Getter @Setter
	Backend backend;
	
	@Getter @Setter
	Publishers publishers;
	
	@Getter @Setter
	Signer signer;
	
	
	public static class RegistrationManagerBuilder {
		
		public RegistrationManagerBuilder publishers(Publishers publishers) {
			this.publishers = publishers;
			return this;
		}
		
		public RegistrationManagerBuilder publishers(String ...addresses) {
			this.publishers = new SimplePublishers(addresses);
			return this;
		}
		
	}
	
	
	public boolean checkRegistration(String accountAddress) throws IOException {
		return this.getBackend().checkRegistration(accountAddress);
	}
	
	public Entry fetchRegistration(String accountAddress) throws IOException {
		return this.getBackend().fetchRegistration(accountAddress);
	}
	
	public String recordRegistration(Entry account) throws IOException {
		return this.getBackend().recordRegistration(signer, account);
	}
	
	public String revokeRegistration(String accountAddress) throws IOException {
		return this.getBackend().revokeRegistration(signer, accountAddress);
	}

	public String getReceipt(String txHash) throws IOException {
		return getBackend().getReceipt(txHash);
	}
	
}
