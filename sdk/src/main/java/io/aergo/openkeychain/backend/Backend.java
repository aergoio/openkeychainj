/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.backend;

import java.io.Closeable;
import java.io.IOException;

import hera.api.model.ContractInterface;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.model.Entry;

public interface Backend extends Closeable {
	
	public <T extends Closeable> T getAdaptor(Class<T> clazz);

	public String getContractAddress();
	public void setContract(ContractInterface contract);
	public void setContract(String contractAddress);
	
	public String getReceipt(String txHash) throws IOException;

	public String getRootAddr() throws IOException;
	public String addPublisher(Signer signer, String publisherAddress) throws IOException;
	public String removePublisher(Signer signer, String publisherAddress) throws IOException;
	public String[] getPublishers() throws IOException;
	public Entry getPublisher(String publisherAddress) throws IOException;
	public boolean isPublisher(String publisherAddress) throws IOException;
	
	public String recordRegistration(Signer signer, Entry account) throws IOException;
	public String revokeRegistration(Signer signer, String accountAddress) throws IOException;
	public Entry fetchRegistration(String accountAddress) throws IOException;
	public boolean checkRegistration(String accountAddress) throws IOException;
	
}
