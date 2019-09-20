/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.backend;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.type.TypeReference;

import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClient;
import hera.util.HexUtils;
import hera.util.Sha256Utils;
import io.aergo.openkeychain.core.Signer;
import io.aergo.openkeychain.model.Entry;
import io.aergo.openkeychain.util.Jsonizer;
import lombok.Builder;
import lombok.Cleanup;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;


@Builder
public class AergoBackend implements Backend {
	
	static final Logger logger = LoggerFactory.getLogger(AergoBackend.class);
			
	
	@NonNull @Getter @Setter
	AergoAdaptor aergoAdaptor;
	
	@Getter
	ContractInterface contract;
	
	
	public static class AergoBackendBuilder {
		
		public AergoBackendBuilder aergoClient(AergoClient client) {
			this.aergoAdaptor = new AergoAdaptor(client);
			return this;
		}
		
		public AergoBackendBuilder contractAddress(String contractAddress) {
			if (contractAddress != null) {
				this.contract = this.aergoAdaptor.getAergoClient()
						.getContractOperation()
						.getContractInterface(ContractAddress.of(contractAddress));
			}
			return this;
		}
		
	}
	
	
	
	@Override
	public void close() {
		this.getAergoAdaptor().close();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Closeable> T getAdaptor(Class<T> clazz) {
		return (T)this.getAergoAdaptor();
	}
	
	
	public String getContractAddress() {
		return contract.getAddress().getEncoded();
	}
	
	public void setContract(ContractInterface contract) {
		this.contract = contract;
	}
	
	public void setContract(String contractAddress) {
		this.contract = aergoAdaptor.getAergoClient()
				.getContractOperation()
				.getContractInterface(ContractAddress.of(contractAddress));
	}
	
	
	public String getReceipt(String txHash) {
		ContractTxReceipt receipt = this.getAergoAdaptor().getContractReceipt(ContractTxHash.of(txHash));
		StringBuilder sb = new StringBuilder();
		sb.append(receipt.getStatus());
		if (!receipt.getRet().isEmpty()) {
			sb.append(':').append(receipt.getRet());
		}
		return sb.toString();
	}
	
	
	public String getRootAddr() throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "getRootAddr");
		return result.bind(String.class);
	}
	
	
	public String addPublisher(Signer signer, String publisherAddress) throws IOException {
		Entry publisher = Entry.of(publisherAddress);
		publisher.sign(signer);
		String entry = publisher.marshal();
		byte[] sign = signer.sign(Sha256Utils.digest(entry.getBytes(StandardCharsets.UTF_8)));
		ContractTxHash txHash = this.getAergoAdaptor().executeContract(
				signer, this.getContract(),
				"addPublisher", entry, HexUtils.encode(sign));
		return txHash.getEncoded();
	}
	
	public String removePublisher(Signer signer, String publisherAddress) throws IOException {
		ContractTxHash txHash = this.getAergoAdaptor().executeContract(
				signer, this.getContract(),
				"removePublisher", publisherAddress);
		return txHash.getEncoded();
	}
	
	public String[] getPublishers() throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "getPublishers");
		String res = result.toString();
		if (res.equals("{}")) {
			return new String[] {};
		}
		@Cleanup InputStream in = new ByteArrayInputStream(res.getBytes(StandardCharsets.UTF_8));
		return Jsonizer.getInstance().getMapper().reader()
				.forType(new TypeReference<String[]>() {})
				.readValue(in);
	}
	
	public Entry getPublisher(String publisherAddress) throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "getPublisher", publisherAddress);
		@Cleanup InputStream in = result.getResultInRawBytes().getInputStream();
		return Jsonizer.getInstance().getMapper().reader()
				.forType(Entry.class)
				.readValue(in);
	}
	
	public boolean isPublisher(String publisherAddress) throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "isPublisher", publisherAddress);
		return result.bind(Boolean.class);
	}
	
	
	public String recordRegistration(Signer signer, Entry account) throws IOException {
		account.sign(signer);
		String entry = account.marshal();
		byte[] sign = signer.sign(Sha256Utils.digest(entry.getBytes(StandardCharsets.UTF_8)));
		ContractTxHash txHash = this.getAergoAdaptor().executeContract(
				signer, this.getContract(), "recordRegistration", entry, HexUtils.encode(sign));
		return txHash.getEncoded();
	}
	
	public String revokeRegistration(Signer signer, String accountAddress) throws IOException {
		ContractTxHash txHash = this.getAergoAdaptor().executeContract(
				signer, this.getContract(), "revokeRegistration", accountAddress);
		return txHash.getEncoded();
	}
	
	public Entry fetchRegistration(String accountAddress) throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "fetchRegistration", accountAddress);
		return result.bind(Entry.class);
	}
	
	public boolean checkRegistration(String accountAddress) throws IOException {
		ContractResult result = this.getAergoAdaptor().queryContract(
				this.getContract(), "checkRegistration", accountAddress);
		return result.bind(Boolean.class);
	}
	
}
