package com.example.ethhashapp.service;

import com.example.ethhashapp.config.ApplicationConfig;
import com.example.ethhashapp.entities.Diploma;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Uint;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.TransactionReceiptProcessor;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class EthereumService {

    private final ApplicationConfig applicationConfig;

    public EthereumService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    private Web3j web3;

    /**
     * Connect to the Ethereum blockchain.
     */
    public void connect() {
        web3 = Web3j.build(new HttpService(applicationConfig.getEthHost()));
    }

    /**
     * Disconnect to the Ethereum blockchain.
     */
    public void disconnect() {
        web3.shutdown();
    }

    /**
     * Get version of the Ethereum client.
     *
     * @return The client version as {@link String}
     */
    public String getClientVersion() {
        try {
            final Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
            return web3ClientVersion.getWeb3ClientVersion();
        } catch (IOException ex) {
            throw new RuntimeException("Error while sending json-rpc requests to get client version", ex);
        }
    }

    /**
     * Get the current block number.
     *
     * @return The current block number as {@link BigInteger}
     */
    public BigInteger getBlockNumber() {
        try {
            final EthBlockNumber ethBlockNumber = web3.ethBlockNumber().send();
            return ethBlockNumber.getBlockNumber();
        } catch (IOException ex) {
            throw new RuntimeException("Error while sending json-rpc requests to get block number", ex);
        }
    }

    /**
     * Get the current gas price.
     *
     * @return The current gas price as {@link BigInteger}
     */
    public BigInteger getGasPrice() {
        try {
            final EthGasPrice ethGasPrice = web3.ethGasPrice().send();
            return ethGasPrice.getGasPrice();
        } catch (IOException ex) {
            throw new RuntimeException("Error while sending json-rpc requests to get gas price", ex);
        }
    }

    /**
     * Get the current balance of the account (specified in properties) in ETH.
     *
     * @return The current account balance in ETH as {@link BigDecimal}
     */
    public BigDecimal getBalance() {
        try {
            EthGetBalance web3ClientVersion = web3.ethGetBalance(applicationConfig.getAccountAddress(), DefaultBlockParameter.valueOf("latest")).send();
            return Convert.fromWei(web3ClientVersion.getBalance().toString(), Convert.Unit.ETHER);
        } catch (IOException ex) {
            throw new RuntimeException("Error while sending json-rpc requests to get address balance", ex);
        }
    }

    /**
     * Retrieve some information data of a stored document in the Ethereum blockchain.<br>
     * It uses the SHA256 hash to call the smart contract to get the data for that document.
     *
     * @return The document mine time (Epoch time) as {@link BigInteger}, or 0 if not found
     * @throws ExecutionException   Error while calling smart contract
     * @throws InterruptedException Error while calling smart contract
     */
    public BigInteger getDataFromEthereum(Diploma diploma) throws ExecutionException, InterruptedException {

        // generate SHA256
        final byte[] sha256hex = DigestUtils.sha256(diploma.getFile().getFile());

        // contract function to call with parameters
        final Function function = new Function(
                "findDocHash",
                List.of(new Bytes32(sha256hex)),
                List.of(new TypeReference<Uint>() {
                })
        );

        // encode function values in transaction data format
        final String encodedFunction = FunctionEncoder.encode(function);

        final String accountAddress = applicationConfig.getAccountAddress();
        final String contractAddress = applicationConfig.getContractAddress();

        // call contract function
        EthCall response = web3.ethCall(
                Transaction.createEthCallTransaction(accountAddress, contractAddress, encodedFunction),
                DefaultBlockParameterName.LATEST
        ).sendAsync().get();

        // get response value
        List<Type> results = FunctionReturnDecoder.decode(response.getValue(), function.getOutputParameters());

        return (BigInteger) results.get(0).getValue();

    }

    /**
     * Save the specified {@code diploma} hass to the Ethereum blockchain.
     *
     * @param diploma        The document to be hashed and stored
     * @param fromPrivateKey The private key of the transaction sender
     * @throws ExecutionException   Error while calling smart contract
     * @throws InterruptedException Error while calling smart contract
     * @throws IOException          Error while calling smart contract
     * @throws TransactionException Error while calling smart contract
     */
    public void saveHashToEthereum(Diploma diploma, String fromPrivateKey) throws ExecutionException, InterruptedException, IOException, TransactionException {

        // generate SHA256
        final byte[] sha256hex = DigestUtils.sha256(diploma.getFile().getFile());

        // contract function to call with parameters
        final Function function = new Function(
                "addDocHash",
                List.of(new Bytes32(sha256hex)),
                Collections.emptyList()
        );

        // create credentials
        final Credentials credentials = Credentials.create(fromPrivateKey);
        log.info("Account address: " + credentials.getAddress());
        log.info("Balance: " + Convert.fromWei(web3.ethGetBalance(credentials.getAddress(), DefaultBlockParameterName.LATEST).send().getBalance().toString(), Convert.Unit.ETHER));

        // encode function values in transaction data format
        final String encodedFunction = FunctionEncoder.encode(function);

        final String contractAddress = applicationConfig.getContractAddress();

        // use a wallet (credential) to create and sign transaction
        final TransactionManager txManager = new FastRawTransactionManager(web3, credentials);

        // send the transaction
        final EthSendTransaction ethSendTransaction = txManager.sendTransaction(
                BigInteger.valueOf(20000000000L),
                BigInteger.valueOf(6721975),
                contractAddress,
                encodedFunction,
                BigInteger.ZERO);

        if (ethSendTransaction.getError() != null) {
            log.info(ethSendTransaction.getError().getMessage());
        } else {

            final String txHash = ethSendTransaction.getTransactionHash();

            // wait for transaction to be mined
            final TransactionReceiptProcessor receiptProcessor = new PollingTransactionReceiptProcessor(
                    web3,
                    TransactionManager.DEFAULT_POLLING_FREQUENCY,
                    TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH);

            final TransactionReceipt txReceipt = receiptProcessor.waitForTransactionReceipt(txHash);

            log.info(txReceipt.getTransactionHash());

        }
    }

}
