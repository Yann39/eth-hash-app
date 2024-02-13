package com.example.ethhashapp.vm;

import com.example.ethhashapp.entities.Attachment;
import com.example.ethhashapp.entities.Diploma;
import com.example.ethhashapp.service.DiplomaManager;
import com.example.ethhashapp.service.EthereumService;
import com.example.ethhashapp.utils.ApplicationConstants;
import com.example.ethhashapp.utils.ApplicationUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.*;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.select.annotation.VariableResolver;
import org.zkoss.zk.ui.select.annotation.WireVariable;
import org.zkoss.zkplus.spring.DelegatingVariableResolver;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Messagebox;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yann39
 * @since 1.0.0
 */
@VariableResolver(DelegatingVariableResolver.class)
@Slf4j
public class DiplomaVm {

    //region Variables
    @Getter
    private List<Diploma> diplomas;

    @Getter
    private String clientVersion;

    @Getter
    private BigInteger blockNumber;

    @Getter
    private BigInteger gasPrice;

    @Getter
    private BigDecimal balance;

    @WireVariable
    private DiplomaManager diplomaManager;

    @WireVariable
    EthereumService ethereumService;

    private Web3j web3;
    //endregion

    //region Init
    @Init
    public void init() {
        log.info("Init " + this.getClass().getSimpleName());

        // load the diploma list from database
        loadDiplomas();
    }
    //endregion

    //region Private methods

    /**
     * Get the diploma list from the database.<br>
     * It can be filtered by the current filter value.
     *
     * @return A list of {@link Diploma} elements representing the diplomas
     */
    private List<Diploma> getDiplomaListFromDatabase() {
        return diplomaManager.getAll();
    }

    //endregion

    //region Command methods

    /**
     * Reload the diploma list from the database.
     *
     * @see Diploma
     */
    @Command
    @NotifyChange("diplomas")
    public void loadDiplomas() {
        diplomas = getDiplomaListFromDatabase();
    }

    /**
     * Add a logo to the current logo list.<br>
     * Used to add a row in the GUI.
     *
     * @see Diploma
     */
    @Command
    @NotifyChange("diplomas")
    public void addDiploma() {
        final Diploma diploma = new Diploma();
        diplomas.add(0, diploma);
    }

    /**
     * Remove the specified diploma from the current diploma list.<br>
     * Used to remove a row from the GUI.
     *
     * @param diploma The {@link Diploma} representing the diploma to be removed
     * @see Diploma
     */
    @Command
    @NotifyChange("diplomas")
    public void removeDiploma(@BindingParam("diploma") Diploma diploma) {
        diplomas.remove(diploma);
    }

    /**
     * Save the current diploma list to the database.
     *
     * @see Diploma
     */
    @Command
    @NotifyChange("diplomas")
    public void saveDiplomas() {
        try {
            // confirmation dialog
            final EventListener<Messagebox.ClickEvent> clickListener = event -> {
                if (Messagebox.Button.YES.equals(event.getButton())) {

                    // title is mandatory
                    if (diplomas.stream().anyMatch(i -> (i.getTitle() == null || i.getTitle().trim().isEmpty()))) {
                        ApplicationUtils.showWarning(Labels.getLabel("message.documentTitleMandatory"));
                        return;
                    }

                    // store ids of the new edited list
                    final List<Long> diplomaIds = new ArrayList<>();
                    for (Diploma is : diplomas) {
                        // if title is empty, set it to null
                        if (is.getTitle() != null && is.getTitle().isEmpty()) {
                            is.setTitle(null);
                        }
                        diplomaIds.add(is.getId());
                    }

                    // loop through all database values and remove the ones that or not in the new list
                    for (Diploma i : diplomaManager.getAll()) {
                        if (!diplomaIds.contains(i.getId())) {
                            // if diploma is still used somewhere
                            if (diplomaManager.isUsed(i.getId())) {
                                // show notification
                                ApplicationUtils.showWarning(Labels.getLabel("message.documentStillUsed", new Object[]{i.getTitle()}));
                                return;
                            } else {
                                diplomaManager.delete(i);
                            }
                        }
                    }

                    // update the database with the new values
                    for (Diploma is : diplomas) {
                        diplomaManager.saveOrUpdate(is);
                    }

                    // show notification
                    ApplicationUtils.showInfo(Labels.getLabel("message.documentListSaved"));
                }
            };
            Messagebox.show(Labels.getLabel("message.saveModificationsConfirmation"), "Confirmation", new Messagebox.Button[]{Messagebox.Button.YES, Messagebox.Button.NO}, Messagebox.QUESTION, clickListener);
        } catch (Exception e) {
            log.error("Error when saving diploma list : " + e.getMessage());
        }
    }

    /**
     * Set the selected media as logo signature.
     *
     * @param event The ZK {@link UploadEvent} event
     */
    @Command
    public void addFileAttachment(@ContextParam(ContextType.TRIGGER_EVENT) UploadEvent event, @BindingParam("diploma") Diploma diploma) {
        final Media media = event.getMedia();
        if (media != null) {
            final Attachment file = new Attachment();
            file.setFileName(media.getName());
            file.setFile(media.getByteData());
            file.setUploadDate(LocalDateTime.now());
            diploma.setFile(file);
            BindUtils.postNotifyChange(null, null, diploma, "file");
        }
    }

    /**
     * Remove the signature file attached to the logo.
     */
    @Command
    public void removeFileAttachment(@BindingParam("diploma") Diploma diploma) {
        diploma.setFile(null);
        BindUtils.postNotifyChange(null, null, diploma, "file");
    }

    /**
     * Prompt download of the given {@code file} using the specified {@code filename}.
     *
     * @param file     The file data as byte array
     * @param filename The file name as {@link String}
     */
    @Command
    public void downloadFile(@BindingParam("file") byte[] file, @BindingParam("filename") String filename) {
        try {
            // normalize filename
            filename = Normalizer.normalize(filename, Normalizer.Form.NFD);
            // replace all non ASCII characters
            filename = filename.replaceAll("[^\\x00-\\x7F]", "");
            // prompt download
            Filedownload.save(file, ApplicationConstants.MIME_TYPE_OCTET, filename);
        } catch (Exception e) {
            log.error("An error occurred while downloading file : " + e.getMessage());
        }
    }

    /**
     * Open a dialog to view the specified {@code diploma}.
     *
     * @param diploma The {@link Diploma} to be viewed
     */
    @Command
    public void openViewNotificationDialog(@BindingParam("diploma") Diploma diploma) {
        // pass the diploma as parameter
        final Map<String, Object> args = new HashMap<>();
        args.put("diploma", diploma);

        // open dialog
        Executions.createComponents(ApplicationConstants.VIEW_DIPLOMA_DIALOG, null, args);
    }

    @Command
    @NotifyChange({"clientVersion", "blockNumber", "gasPrice"})
    public void connectToEthereum() {
        ethereumService.connect();
        clientVersion = ethereumService.getClientVersion();
        blockNumber = ethereumService.getBlockNumber();
        gasPrice = ethereumService.getGasPrice();
        balance = ethereumService.getBalance();
    }

    @Command
    @NotifyChange({"clientVersion", "blockNumber", "gasPrice"})
    public void disconnectFromEthereum() {
        ethereumService.disconnect();
        clientVersion = null;
        blockNumber = null;
        gasPrice = null;
    }
    //endregion

}