/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.core;

import java.util.Collection;
import java.util.Set;

public interface Publishers {
	
	public void addPublisher(Collection<? extends String> pubsAddresses);
	public void addPublisher(String ...pubsAddresses);
	public void removePublisher(String ...pubsAddresses);
	public boolean isPublisher(String pubsAddress);
	public Set<String> getPublishers();
	
}
