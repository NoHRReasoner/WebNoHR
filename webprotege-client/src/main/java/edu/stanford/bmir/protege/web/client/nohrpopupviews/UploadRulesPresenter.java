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
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadRulesAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.nohrrules.RuleAlreadyExistException;
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
public class UploadRulesPresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final UploadRulesDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    private final String extention = ".nohr";

    @Inject
    public UploadRulesPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                                @Nonnull ProjectId projectId,
                                @Nonnull UploadRulesDialogView view,
                                @Nonnull ModalManager modalManager,
                                @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.messages = checkNotNull(messages);
    }

    public void uploadRules(@Nonnull RulesUploadHandler rulesUpdateHandler,
                            @Nonnull ActionFactory actionFactory) {
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Upload");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUploadRules(actionFactory, rulesUpdateHandler, closer);
        });
        modalManager.showModal(modalPresenter);

    }


    private void handleUploadRules(@Nonnull ActionFactory actionFactory,
                                   @Nonnull RulesUploadHandler rulesUploadHandler, ModalCloser closer) throws RuleAlreadyExistException {

        String filename = view.getFile().getFilename();
        String fileExtention = filename.substring(filename.length() - extention.length(), filename.length());

        if (!view.isFileUploadSpecified()) {
            closer.closeModal();
            rulesUploadHandler.handleRulesUploaded(null, 0, NohrResponseCodes.NONFILE_ERROR);
        } else if (!fileExtention.equals(extention)) {
            closer.closeModal();
            rulesUploadHandler.handleRulesUploaded(null, 0, NohrResponseCodes.INVALIDFILE_ERROR);
        } else {
            String postUrl = GWT.getModuleBaseURL() + "submitfile";
            view.setFileUploadPostUrl(postUrl);
            ProgressMonitor.get().showProgressMonitor("Uploading sources", "Uploading file");
            view.setSubmitCompleteHandler(event -> {
                ProgressMonitor.get().hideProgressMonitor();
                handleSourcesUploadComplete(event, actionFactory, rulesUploadHandler, closer);
            });
            view.submitFormData();
        }
    }

    private void handleSourcesUploadComplete(FormPanel.SubmitCompleteEvent event, ActionFactory actionFactory, RulesUploadHandler rulesUploadHandler, ModalCloser closer) {
        FileUploadResponse response = new FileUploadResponse(event.getResults());
        if (response.wasUploadAccepted()) {
            DocumentId documentId = response.getDocumentId();

            UploadRulesAction action = actionFactory.createAction(projectId,
                    documentId.getDocumentId());
            dispatchServiceManager.execute(action,
                    result -> rulesUploadHandler.handleRulesUploaded(result.getRules(), result.getLineNumber(), result.getCode()));
            closer.closeModal();

        } else {
            rulesUploadHandler.handleRulesUploaded(null, 0, NohrResponseCodes.UNKNOWN_ERROR);
            closer.closeModal();
        }
    }

    public interface ActionFactory {


        UploadRulesAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String file);
    }


    public interface RulesUploadHandler {
        void handleRulesUploaded(List<NohrRule> newRule, Integer lineNumber, NohrResponseCodes code);
    }
}
