/*
 * @copyright defined in LICENSE.txt
 */

package io.aergo.openkeychain.backend;

import java.io.Closeable;

import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.BytesValue;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.Fee;
import hera.api.model.RawTransaction;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.exception.HerajException;
import hera.key.Signer;
import hera.spec.resolver.TransactionHashResolver;
import hera.transaction.NonceProvider;
import hera.transaction.SimpleNonceProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class AergoAdaptor implements Closeable {
	
	@NonNull @Getter @Setter
	AergoClient aergoClient;
	
	@NonNull @Getter @Setter
	NonceProvider nonceProvider;
	
	
	public AergoAdaptor(AergoClient aergoClient) {
		this(aergoClient, new SimpleNonceProvider());
		this.aergoClient.cacheChainIdHash(
				this.aergoClient.getBlockchainOperation().getChainIdHash());
	}
	
	
	@Override
	public void close() {
		this.aergoClient.close();
	}
	
	
	@RequiredArgsConstructor
	public class HeraTxSigner implements hera.key.Signer {
		
		@NonNull @Getter
		final io.aergo.openkeychain.core.Signer signer;
		
		@Override
		public AccountAddress getPrincipal() {
			return AccountAddress.of(this.getSigner().getAddress());
		}
		
		@Override
		public Transaction sign(RawTransaction rawTransaction) {
			try {
				final TxHash withoutSignature = TransactionHashResolver.calculateHash(rawTransaction);
				final byte[] hashedMessage = withoutSignature.getBytesValue().getValue();
				final byte[] signBytes = this.getSigner().sign(hashedMessage);
				final Signature signature = Signature.of(BytesValue.of(signBytes));
				final TxHash withSignature = TransactionHashResolver.calculateHash(rawTransaction, signature);
				return new Transaction(rawTransaction, signature, withSignature);
			} catch (HerajException e) {
				throw e;
			} catch (Exception e) {
				throw new HerajException(e);
			}
		}
		
	}
	
	
	public void bindNonce(String account) {
		this.getNonceProvider().bindNonce(
				getAergoClient().getAccountOperation()
				.getState(AccountAddress.of(account)));
	}
	
	public AccountState getState(String account) {
		// get state
		AccountState state = getAergoClient().getAccountOperation()
				.getState(AccountAddress.of(account));
		// bind nonce
		this.getNonceProvider().bindNonce(state);
		return state;
	}
	
	public Transaction getTransaction(TxHash txHash) {
		return getAergoClient().getTransactionOperation().getTransaction(txHash);
	}
	
	public TxHash sendcoin(final io.aergo.openkeychain.core.Signer from, String to, Aer amount) {
		// signer
		final Signer signer = new HeraTxSigner(from);
		
		// raw transaction
		final RawTransaction rawTx = RawTransaction.newBuilder()
				.chainIdHash(getAergoClient().getCachedChainIdHash())
				.from(signer.getPrincipal())
				.to(AccountAddress.of(to))
				.amount(amount)
				.nonce(getNonceProvider().incrementAndGetNonce(signer.getPrincipal()))
				.build();
		final Transaction signedTx = signer.sign(rawTx);
		
		// commit transaction
		return getAergoClient().getTransactionOperation().commit(signedTx);
	}
	
	
	public ContractTxHash deployContract(final io.aergo.openkeychain.core.Signer owner,
			String encodedContract, Object ...args) {
		// signer
		final Signer signer = new HeraTxSigner(owner);
		
		// contract definition
		final ContractDefinition definition = ContractDefinition.newBuilder()
				.encodedContract(encodedContract)
				.constructorArgs(args)
				.build();

		// definition transaction
		return getAergoClient().getContractOperation().deploy(signer,
				definition, getNonceProvider().incrementAndGetNonce(signer.getPrincipal()),
				Fee.getDefaultFee());
	}
	
	
	public ContractTxHash executeContract(final io.aergo.openkeychain.core.Signer owner,
			ContractInterface contract, String funcName, Object ...args) {
		// signer
		final Signer signer = new HeraTxSigner(owner);
		
		// contract execution
		final ContractInvocation execution = contract.newInvocationBuilder()
				.function(funcName)
				.args(args)
				.build();
		
		// execution transaction
		return getAergoClient().getContractOperation().execute(signer,
				execution, getNonceProvider().incrementAndGetNonce(signer.getPrincipal()),
				Fee.getDefaultFee());
	}
	
	
	public ContractTxReceipt getContractReceipt(ContractTxHash txHash) {
		// contract receipt
		return getAergoClient().getContractOperation().getReceipt(txHash);
	}
	
	
	public ContractResult queryContract(ContractInterface contract, String funcName, Object ...args) {
		// contract query
		final ContractInvocation query = contract.newInvocationBuilder()
				.function(funcName)
				.args(args)
				.build();
		
		// query result
		return getAergoClient().getContractOperation().query(query);
	}

}
