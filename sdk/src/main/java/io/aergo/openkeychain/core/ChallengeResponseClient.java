/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Response;
import io.aergo.openkeychain.provider.TimestampProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@AllArgsConstructor
public abstract class ChallengeResponseClient extends ChallengeResponse {
	
	@Getter @Setter
	long thresholdMinutes;
	
	@NonNull @Getter @Setter
	TimestampProvider timestampProvider;
	
	
	public ChallengeResponseClient() {
		this(30L, TimestampProvider.defaultProvider);
	}
	
	
	public Response createResponse(Challenge challenge) {
		String timestamp = this.getTimestampProvider().getTimestamp();
		Response response = new Response(timestamp, challenge);
		response.sign(this.getSigner());
		return response;
	}
	
}
