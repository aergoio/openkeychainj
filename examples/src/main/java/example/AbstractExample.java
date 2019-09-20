package example;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hera.api.model.Aer;
import hera.client.AergoClient;
import hera.client.AergoClientBuilder;
import hera.key.AergoKey;
import hera.api.model.TxHash;
import io.aergo.openkeychain.backend.AergoAdaptor;
import io.aergo.openkeychain.core.KeyManager;
import io.aergo.openkeychain.core.SimpleKeyManager;
import io.aergo.openkeychain.util.PropertiesUtils;

public abstract class AbstractExample {
	
	static final Logger logger = LoggerFactory.getLogger(AbstractExample.class);


	protected Properties config;
	
	protected AbstractExample() {
		try {
			this.config = PropertiesUtils.load(getClass(), "/config.xml");
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String config(final String key) {
		String sysprop = System.getProperty(key);
		if (sysprop != null) {
			return sysprop;
		}
		return config.getProperty(key);
	}
	
	protected void fund(String accountAddress, Aer amount) {
		final AergoClient client = new AergoClientBuilder()
				.withEndpoint(config("aergo.endpoint"))
				.withNonBlockingConnect()
				.withRetry(4, 500, TimeUnit.MILLISECONDS)
				.build();
		final AergoAdaptor adaptor = new AergoAdaptor(client);
		
		final KeyManager charger = new SimpleKeyManager(AergoKey.of(
				config("charger.encrypted"), config("charger.password")));
		adaptor.bindNonce(charger.fetchAddress());
		final TxHash txHash = adaptor.sendcoin(charger.getSigner(),
				accountAddress, amount);
		
		awaitUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return adaptor.getTransaction(txHash).isConfirmed();
			}
		});
		adaptor.close();
	}
	
	protected void awaitUntil(Callable<Boolean> conditionEvaluator) {
		Awaitility.await().atMost(5, TimeUnit.SECONDS)
				.pollInterval(200, TimeUnit.MILLISECONDS)
				.until(conditionEvaluator);
	}
	
	protected void awaitUntilTxConfirmed(final AergoAdaptor adaptor, final String txHash) {
		awaitUntil(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				return adaptor.getTransaction(TxHash.of(txHash)).isConfirmed();
			}
		});
	}
	
	public abstract void run() throws Exception;

}
