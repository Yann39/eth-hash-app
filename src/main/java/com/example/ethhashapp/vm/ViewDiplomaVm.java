package com.example.ethhashapp.vm;

import com.example.ethhashapp.entities.Diploma;
import com.example.ethhashapp.service.EthereumService;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.web3j.protocol.exceptions.TransactionException;
import org.zkoss.bind.annotation.*;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Window;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ExecutionException;

/**
 * View-model for document view dialog.
 *
 * @author Yann39
 * @since 1.0.0
 */
@VariableResolver(DelegatingVariableResolver.class)
@Slf4j
public class ViewDiplomaVm {

    @Getter
    private Diploma diploma;

    @Getter
    private String sha256hex;

    @Getter
    private LocalDateTime storageDate;

    @Getter
    @Setter
    private String fromPrivateKey;

    @WireVariable
    EthereumService ethereumService;

    @Wire("#viewDocumentDialog")
    private Window viewDocumentDialog;

    @Init
    public void init(@ContextParam(ContextType.VIEW) Component view,
                     @ExecutionArgParam("diploma") Diploma diploma) {
        log.info("Init " + this.getClass().getSimpleName());

        // to be able to wire UI components
        Selectors.wireComponents(view, this, false);

        // keep passed diploma
        this.diploma = diploma;

        // generate the SHA256 hash
        sha256hex = DigestUtils.sha256Hex(diploma.getFile().getFile());

        // try to retrieve information from Ethereum if the document is already on chain
        retrieveValuesFromEthereum();
    }

    /**
     * Closes the dialog.
     */
    @Command
    public void closeDialog() {
        viewDocumentDialog.detach();
    }

    /**
     * Store the hash of the current diploma document to the Ethereum blockchain.
     */
    @Command
    @NotifyChange("storageDate")
    public void saveHashToEthereum() {
        try {
            if (fromPrivateKey != null) {
                ethereumService.saveHashToEthereum(diploma, fromPrivateKey);
                // update values got from chain
                retrieveValuesFromEthereum();
            }
        } catch (ExecutionException | InterruptedException | IOException | TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieve diploma document information from Ethereum.
     */
    public void retrieveValuesFromEthereum() {
        try {
            final BigInteger date = ethereumService.getDataFromEthereum(diploma);
            if (date.longValue() > 0) {
                storageDate = LocalDateTime.ofEpochSecond(date.longValue(), 0, ZoneOffset.UTC);
            } else {
                storageDate = null;
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}