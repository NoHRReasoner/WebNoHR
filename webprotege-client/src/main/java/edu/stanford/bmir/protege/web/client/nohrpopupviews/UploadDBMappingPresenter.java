package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FormPanel;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.progress.ProgressMonitor;
import edu.stanford.bmir.protege.web.client.upload.FileUploadResponse;
import edu.stanford.bmir.protege.web.shared.csv.DocumentId;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadDBMappingAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.DBMappingAlreadyExistException;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UploadDBMappingPresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final UploadDBMappingDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    private final String extention = "";

    //uncomment to change file extension accepted by the client to send it to server
    /*private final String extention = ".nohr";*/

    @Inject
    public UploadDBMappingPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull UploadDBMappingDialogView view,
                                    @Nonnull ModalManager modalManager,
                                    @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.messages = checkNotNull(messages);
    }

    public void uploadDBMapping(@Nonnull DBMappingUploadHandler dbMappingUpdateHandler,
                            @Nonnull ActionFactory actionFactory) {
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Upload");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUploadDBMapping(actionFactory, dbMappingUpdateHandler, closer);
        });
        modalManager.showModal(modalPresenter);

    }


    private void handleUploadDBMapping(@Nonnull ActionFactory actionFactory,
                                   @Nonnull DBMappingUploadHandler dbMappingUploadHandler, ModalCloser closer) throws DBMappingAlreadyExistException {

        String filename = view.getFile().getFilename();
        String fileExtention = filename.substring(filename.length() - extention.length(), filename.length());

        if (!view.isFileUploadSpecified()) {
            closer.closeModal();
            dbMappingUploadHandler.handleDBMappingUploaded(null,null, 0, NohrResponseCodes.NONFILE_ERROR);
        } else if (!fileExtention.equals(extention)) {
            closer.closeModal();
            dbMappingUploadHandler.handleDBMappingUploaded(null,null, 0, NohrResponseCodes.INVALIDFILE_ERROR);
        } else {
            String postUrl = GWT.getModuleBaseURL() + "submitfile";
            view.setFileUploadPostUrl(postUrl);
            view.setSubmitCompleteHandler(event -> {
                ProgressMonitor.get().hideProgressMonitor();
                handleSourcesUploadComplete(event, actionFactory, dbMappingUploadHandler, closer);
            });
            view.submitFormData();
        }
    }

    private void handleSourcesUploadComplete(FormPanel.SubmitCompleteEvent event, ActionFactory actionFactory, DBMappingUploadHandler dbMappingUploadHandler, ModalCloser closer) {
        FileUploadResponse response = new FileUploadResponse(event.getResults());
        if (response.wasUploadAccepted()) {
            DocumentId documentId = response.getDocumentId();

            UploadDBMappingAction action = actionFactory.createAction(projectId,
                    documentId.getDocumentId());
            dispatchServiceManager.execute(action,
                    result -> dbMappingUploadHandler.handleDBMappingUploaded(result.getDBMappings(), result.getUIMappings(), result.getLineNumber(), result.getCode()));
            closer.closeModal();
        } else {
            dbMappingUploadHandler.handleDBMappingUploaded(null,null, 0, NohrResponseCodes.UNKNOWN_ERROR);
            closer.closeModal();
        }
    }

    public interface ActionFactory {


        UploadDBMappingAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String file);
    }


    public interface DBMappingUploadHandler {
        void handleDBMappingUploaded(List<NohrDBMapping> dbMappings, List<NohrDBMapping> uiMappings, Integer lineNumber, NohrResponseCodes code);
    }
}
