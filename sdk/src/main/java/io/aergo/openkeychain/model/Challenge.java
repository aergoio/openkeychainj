/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import hera.util.Base64Utils;
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
public class Challenge extends Signable.AbstractSignable implements Signable {
	
	@JsonView(Signable.class)
	@NonNull @Getter
	String timestamp;
	
	@JsonView(Signable.class)
	@NonNull @Getter
	String context;
	
	@JsonView(Signable.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Getter @Setter
	String certificate;
	
	@JsonView(Signed.class)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Getter @Setter
	String signature;
	
	
	public boolean checkContext(String context) {
		if (this.context == null) {
			return (context == null);
		}
		return this.context.equals(context);
	}
	
	
	public static Challenge decode(String encoded) {
		final byte[] decoded = Base64Utils.decode(encoded);
		return Challenge.unmarshal(new String(decoded, StandardCharsets.UTF_8));
	}
	
	public String encode() {
		return Base64Utils.encode(marshal().getBytes(StandardCharsets.UTF_8));
	}
	
	public static Challenge unmarshal(String serialized) {
		return Jsonizer.getInstance().read(serialized, Challenge.class);
	}
	
}
