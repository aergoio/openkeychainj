/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.backend;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.api.model.ContractInterface;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.model.Entry;

public class MockBackend implements Backend {
	
	static final Logger logger = LoggerFactory.getLogger(MockBackend.class);
	
	
	String rootAddr;
	
	String contractAddress;
	
	
	Set<String> publishers;
	
	Map<String, Entry> users;
	
	
	public MockBackend(String rootAddr) {
		this.rootAddr = rootAddr;
		this.publishers = new HashSet<String>();
		this.publishers.add(rootAddr);
		this.users = new HashMap<String, Entry>();
	}
	
	
	@Override
	public void close() throws IOException {
		// do nothing
	}

	@Override
	public <T extends Closeable> T getAdaptor(Class<T> clazz) {
		return null;
	}



	@Override
	public String getRootAddr() throws IOException {
		return this.rootAddr;
	}

	@Override
	public String getContractAddress() {
		return this.contractAddress;
	}

	@Override
	public void setContract(ContractInterface contract) {
		this.setContract(contract.getAddress().getEncoded());
	}

	@Override
	public void setContract(String contractAddress) {
		this.contractAddress = contractAddress;
	}



	@Override
	public String getReceipt(String txHash) throws IOException {
		return null;
	}



	@Override
	public String addPublisher(Signer signer, String publisherAddress) throws IOException {
		this.publishers.add(publisherAddress);
		return null;
	}

	@Override
	public String removePublisher(Signer signer, String publisherAddress) throws IOException {
		this.publishers.remove(publisherAddress);
		return null;
	}

	@Override
	public String[] getPublishers() throws IOException {
		return this.publishers.toArray(new String[] {});
	}

	@Override
	public Entry getPublisher(String publisherAddress) throws IOException {
		return Entry.of(publisherAddress);
	}

	@Override
	public boolean isPublisher(String publisherAddress) throws IOException {
		return this.publishers.contains(publisherAddress);
	}



	@Override
	public String recordRegistration(Signer signer, Entry account) throws IOException {
		this.users.put(account.getAddr(), account);
		return null;
	}

	@Override
	public String revokeRegistration(Signer signer, String accountAddress) throws IOException {
		if (!this.publishers.contains(signer.getAddress())) {
			return null;
		}
		Entry account = this.users.get(accountAddress);
		if (account == null) {
			return null;
		}
		account.revoke();
		this.users.put(accountAddress, account);
		return null;
	}

	@Override
	public Entry fetchRegistration(String accountAddress) throws IOException {
		return this.users.get(accountAddress);
	}

	@Override
	public boolean checkRegistration(String accountAddress) throws IOException {
		Entry entry = this.users.get(accountAddress);
		if (entry == null || entry.isRevoked()) {
			return false;
		}
		return true;
	}


}