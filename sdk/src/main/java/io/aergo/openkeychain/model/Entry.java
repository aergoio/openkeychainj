/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonView;

import io.aergo.openkeychain.util.Jsonizer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({"addr", "data", "revoked", "publisher", "signature"})
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Entry extends Signable.AbstractSignable implements Signable {
	
	@JsonView(Signable.class)
	@Getter
	private String addr;
	
	@JsonView(Signable.class)
	@Getter @Setter
	private Metadata data;
	
	@JsonView(Signable.class)
	@JsonInclude(JsonInclude.Include.NON_DEFAULT)
	@Getter
	private boolean revoked = false;
	
	@JsonView(Signable.class)
	@JsonProperty("publisher")
	@Getter @Setter
	private String certificate;
	
	@JsonView(Signed.class)
	@Getter @Setter
	private String signature;
	
	
	public Entry(String addr, Metadata data) {
		this.addr = addr;
		this.data = data;
	}
	
	public static final Entry of(String addr) {
		return new Entry(addr, null);
	}

	public static final Entry of(String addr, Metadata data) {
		return new Entry(addr, data);
	}
	
	public static final Entry of(String addr, Metadata data, String certificate) {
		return new Entry(addr, data, false, certificate, null);
	}
	public static final Entry of(String addr, Metadata data, boolean revoked, String certificate) {
		return new Entry(addr, data, revoked, certificate, null);
	}
	
	@JsonProperty("data")
	public String getDataEncoded() {
		if (this.getData() == null) {
			return null;
		}
		return this.getData().encode();
	}
	
	@JsonProperty("data")
	public void setDataEncoded(String encoded) {
		this.setData(MetadataImpl.decode(encoded));
	}
	
	public void revoke() {
		this.revoked = true;
	}
	
	
	public static Entry unmarshal(String source) {
		return Jsonizer.getInstance().read(source, Entry.class);
	}
	
}
