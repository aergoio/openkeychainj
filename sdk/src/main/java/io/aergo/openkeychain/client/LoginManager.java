/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.client;

import io.aergo.openkeychain.core.ChallengeResponseClient;
import io.aergo.openkeychain.core.Publishers;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.core.SimplePublishers;
import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class LoginManager extends ChallengeResponseClient {
	
	@Getter @Setter
	Publishers publishers;
	
	@Getter @Setter
	Signer signer;
	
	
	public static class LoginManagerBuilder {
		
		public LoginManagerBuilder publishers(Publishers publishers) {
			this.publishers = publishers;
			return this;
		}
		
		public LoginManagerBuilder publishers(String ...addresses) {
			this.publishers = new SimplePublishers(addresses);
			return this;
		}
		
	}
	
	
	public Response createResponse(Challenge challenge, Signer signer) {
		this.setSigner(signer);
		return createResponse(challenge);
	}
	
}
