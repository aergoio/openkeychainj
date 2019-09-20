/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Response;
import io.aergo.openkeychain.provider.ContextProvider;
import io.aergo.openkeychain.provider.TimestampProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public abstract class ChallengeResponseServer extends ChallengeResponse {
	
	@Getter @Setter
	long thresholdMinutes;
	
	@NonNull @Getter @Setter
	TimestampProvider timestampProvider;
	
	@NonNull @Getter @Setter
	ContextProvider contextProvider;
	
	
	public ChallengeResponseServer() {
		this(30L, TimestampProvider.defaultProvider, ContextProvider.defaultProvider);
	}
	
	
	public Challenge createChallenge() {
		String timestamp = this.getTimestampProvider().getTimestamp();
		String context = this.getContextProvider().getContext();
		Challenge challenge = new Challenge(timestamp, context);
		challenge.sign(this.getSigner());
		return challenge;
	}
	
	public boolean checkResponse(Response response, String challengeContext) {
		Challenge challenge = response.getChallenge();
		if (challengeContext != null && !challenge.checkContext(challengeContext)) {
			return false;
		}
		if (!this.checkChallenge(challenge)) {
			return false;
		}
		return response.verify();
	}
	
	public boolean checkResponse(Response response) {
		return checkResponse(response, null);
	}
	
}
