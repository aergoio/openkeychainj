/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import io.aergo.openkeychain.core.Publishers;
import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.provider.TimestampProvider;
import io.aergo.openkeychain.util.TimeUtils;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ChallengeResponse {
	
	public abstract Publishers getPublishers();
	public abstract long getThresholdMinutes();
	public abstract TimestampProvider getTimestampProvider();
	public abstract Signer getSigner();
	
	
	public String getAddress() {
		if (this.getSigner() == null) {
			return null;
		}
		return this.getSigner().getAddress();
	}
	
	
	public boolean checkChallenge(Challenge challenge) {
		// check publisher
		if (!this.getPublishers().isPublisher(challenge.getCertificate())) {
			return false;
		}
		// check timestamp
		if (!TimeUtils.within(this.getThresholdMinutes(),
				this.getTimestampProvider(), challenge.getTimestamp())) {
			return false;
		}
		// check signature
		if (!challenge.verify()) {
			return false;
		}
		return true;
	}
	

}
