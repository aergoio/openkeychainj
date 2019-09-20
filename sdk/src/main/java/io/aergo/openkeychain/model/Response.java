/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import io.aergo.openkeychain.util.Jsonizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"timestamp", "context", "certificate", "signature"})
@NoArgsConstructor
@RequiredArgsConstructor
@AllArgsConstructor
public class Response extends Signable.AbstractSignable implements Signable {

	@JsonView(Signable.class)
	@NonNull @Getter
	String timestamp;
	
	@JsonIgnore
	@NonNull @Getter
	Challenge challenge;
	
	@JsonView(Signable.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Getter @Setter
	String certificate;
	
	@JsonView(Signed.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Getter @Setter
	String signature;
	
	
	
	@JsonView(Signable.class)
	@JsonProperty("context")
	public String getContext() {
		return this.challenge.encode();
	}
	
	public void setContext(String context) {
		this.challenge = Challenge.decode(context);
	}
	
	
	public static Response unmarshal(String serialized) {
		return Jsonizer.getInstance().read(serialized, Response.class);
	}
	
}
