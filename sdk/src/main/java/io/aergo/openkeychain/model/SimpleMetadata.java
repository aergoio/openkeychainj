/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import hera.util.Base64Utils;
import io.aergo.openkeychain.util.Jsonizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class SimpleMetadata extends Metadata.AbstractMetadata implements Metadata {
	
	@JsonInclude(JsonInclude.Include.NON_NULL)
	@Getter @Setter
	String value;
	
	
	public static SimpleMetadata unmarshal(String source) {
		return Jsonizer.getInstance().read(source, SimpleMetadata.class);
	}
	
	@Override
	public String marshal() {
		return Jsonizer.getInstance().write(this);
	}
	
	public static SimpleMetadata decode(String encoded) {
		final byte[] bytes = Base64Utils.decode(encoded);
		return unmarshal(new String(bytes, StandardCharsets.UTF_8));
	}
	
}
