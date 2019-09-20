package example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.api.model.Aer;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.client.AergoClientBuilder;
import hera.util.IoUtils;
import io.aergo.openkeychain.backend.AergoAdaptor;
import io.aergo.openkeychain.backend.AergoBackend;
import io.aergo.openkeychain.backend.Backend;
import io.aergo.openkeychain.client.LoginManager;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.SimpleKeyManager;
import io.aergo.openkeychain.model.Challenge;
import io.aergo.openkeychain.model.Entry;
import io.aergo.openkeychain.model.Response;
import io.aergo.openkeychain.model.SimpleMetadata;
import io.aergo.openkeychain.server.RegistrationManager;
import io.aergo.openkeychain.util.KeyUtils;

public class RegistrationExample extends AbstractExample {

	static final Logger logger = LoggerFactory.getLogger(RegistrationExample.class);
	
	KeyManager owner;
	Backend backend;
	
	
	public AergoAdaptor getAergoAdaptor() {
		return this.backend.getAdaptor(AergoAdaptor.class);
	}
	
	File scratch;
	public KeyManager getOwnerKeyManager() {
		return new SimpleKeyManager();
	}
	
	
	public void prepare() throws IOException {
		logger.debug("### prepare");
		
		// aergo backend
		this.backend = AergoBackend.builder()
				.aergoClient(new AergoClientBuilder()
						.withEndpoint(config("aergo.endpoint"))
						.withNonBlockingConnect()
						.withRetry(4, 500, TimeUnit.MILLISECONDS)
						.build())
				.build();
		
		// owner account
		this.owner = getOwnerKeyManager();
		if (getAergoAdaptor().getState(owner.fetchAddress()).getBalance().compareTo(Aer.AERGO_ONE) < 0) {
			fund(owner.fetchAddress(), Aer.of("10", Aer.Unit.AERGO));
		}
		logger.debug("owner: {}", getAergoAdaptor().getState(owner.fetchAddress()));
		
		// deploy contract
		this.backend.setContract(deployContract());
	}
	
	public String deployContract() throws IOException {
		final String encodedContract = new String(IoUtils.from(
				getClass().getResourceAsStream("/contracts/openkeychain.bin")),
				StandardCharsets.UTF_8).trim();
		final ContractTxHash txHash = getAergoAdaptor().deployContract(owner.getSigner(), encodedContract);
		awaitUntilTxConfirmed(getAergoAdaptor(), txHash.getEncoded());
		final ContractTxReceipt receipt = getAergoAdaptor().getContractReceipt(txHash);
		return receipt.getContractAddress().getEncoded();
	}
	
	
	public void after() throws IOException {
		this.backend.close();
	}
	
	
	public void publishers() throws IOException {
		logger.debug("### publishers");
		logger.debug("getRootAddr: {}", backend.getRootAddr());
		logger.debug("isPublisher: {}", backend.isPublisher(owner.fetchAddress()));
		logger.debug("getPublishers: {}", Arrays.toString(backend.getPublishers()));
		
		KeyManager anotherOne = new SimpleKeyManager();
		String addTxHash = backend.addPublisher(owner.getSigner(), anotherOne.fetchAddress());
		logger.debug("addPublisher: {}", addTxHash);
		awaitUntilTxConfirmed(getAergoAdaptor(), addTxHash);
		logger.debug("add.receipt: {}", backend.getReceipt(addTxHash));
		logger.debug("isPublisher: {}", backend.isPublisher(anotherOne.fetchAddress()));
		logger.debug("getPublishers: {}", Arrays.toString(backend.getPublishers()));
		for (String address : backend.getPublishers()) {
			logger.debug("getPublisher: {}", backend.getPublisher(address));
		}
	}
	
	
	public void challenges() throws IOException {
		logger.debug("### challenges");
		String[] publishers = backend.getPublishers();
		
		// LoginManager
		LoginManager loginManager = LoginManager.builder()
				.publishers(publishers)
				.build();
		
		// RegistrationManager
		RegistrationManager regManager = RegistrationManager.builder()
				.backend(backend)
				.publishers(publishers)
				.signer(owner.getSigner())
				.build();
		
		// request challenge
		String challengeSerialized = regManager.createChallenge().marshal();
		logger.debug("challenge: {}", challengeSerialized);
		
		// check challenge
		Challenge challenge = Challenge.unmarshal(challengeSerialized);
		boolean checkChallenge = loginManager.checkChallenge(challenge);
		logger.debug("checkChallenge: {}", checkChallenge);
		
		// create response
		KeyManager clientKeyManager = new SimpleKeyManager();
		String responseSerialized = loginManager.createResponse(challenge, clientKeyManager.getSigner()).marshal();
		logger.debug("response: {}", responseSerialized);
		
		// check response
		Response response = Response.unmarshal(responseSerialized);
		boolean checkResponse = regManager.checkResponse(response);
		logger.debug("checkResponse: {}", checkResponse);
		
		String userCertificate = response.getCertificate();
		logger.debug("userCertificate: {}", userCertificate);
	}
	
	
	public void registrations() throws IOException {
		logger.debug("### registrations");
		// RegistrationManager
		RegistrationManager regManager = RegistrationManager.builder()
				.backend(backend)
				.publishers(backend.getPublishers())
				.signer(owner.getSigner())
				.build();
		
		// new user address
		String userAddress = KeyUtils.createAergoKey().getAddress().getEncoded();
		logger.debug("new userAddress: {}", userAddress);
		
		// record registration
		String recordTxHash = regManager.recordRegistration(Entry.of(userAddress, SimpleMetadata.of("sample")));
		logger.debug("recordRegistration: {}", recordTxHash);
		awaitUntilTxConfirmed(getAergoAdaptor(), recordTxHash);
		logger.debug("record.receipt: {}", regManager.getReceipt(recordTxHash));
		
		// check/fetch registration
		logger.debug("checkRegistration: {}", regManager.checkRegistration(userAddress));
		logger.debug("fetchRegistration: {}", regManager.fetchRegistration(userAddress));
		
		// revoke registration
		String revokeTxHash = regManager.revokeRegistration(userAddress);
		logger.debug("revokeRegistration: {}", revokeTxHash);
		awaitUntilTxConfirmed(getAergoAdaptor(), revokeTxHash);
		logger.debug("revoke.receipt: {}", regManager.getReceipt(revokeTxHash));
		
		// check/fetch registration
		logger.debug("checkRegistration: {}", regManager.checkRegistration(userAddress));
		logger.debug("fetchRegistration: {}", regManager.fetchRegistration(userAddress));
	}
	
	
	@Override
	public void run() throws Exception {
		prepare();
		publishers();
		challenges();
		registrations();
		after();
	}
	
	public static void main(String[] args) {
		try {
			new RegistrationExample().run();
		} catch (Exception e) {
			logger.error("main", e);
		}
	}

}
