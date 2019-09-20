/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class SimplePublishers implements Publishers {
	
	@NonNull @Getter @Setter
	Set<String> publishers;
	
	
	public SimplePublishers() {
		this(new HashSet<String>());
	}
	
	public SimplePublishers(String ...pubsAddresses) {
		this();
		this.addPublisher(pubsAddresses);
	}
	
	
	public void addPublisher(Collection<? extends String> pubsAddresses) {
		this.publishers.addAll(pubsAddresses);
	}
	
	public void addPublisher(String ...pubsAddresses) {
		for (String pubsAddress : pubsAddresses) {
			this.publishers.add(pubsAddress);
		}
	}
	
	public void removePublisher(String ...pubsAddresses) {
		for (String pubsAddress : pubsAddresses) {
			this.publishers.remove(pubsAddress);
		}
	}
	
	public boolean isPublisher(String pubsAddress) {
		return this.publishers.contains(pubsAddress);
	}
	
}
