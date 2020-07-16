package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateDBMappingAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.DBMappingAlreadyExistException;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UpdateDBMappingPresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final UpdateDBMappingDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    @Inject
    public UpdateDBMappingPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull UpdateDBMappingDialogView view,
                                    @Nonnull ModalManager modalManager,
                                    @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.messages = checkNotNull(messages);
    }

    public void updateDBMapping(@Nonnull DBMappingUpdateHandler dbMappingUpdateHandler,
                            @Nonnull String oldDBMapping,
                            @Nonnull ActionFactory actionFactory) {
        view.clear();
        view.setText(oldDBMapping);
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Update");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUpdateDBMapping(view.getText(), oldDBMapping, actionFactory, dbMappingUpdateHandler);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }


    private void handleUpdateDBMapping(@Nonnull String newDBMapping,
                                   @Nonnull String oldDBMapping,
                                   @Nonnull ActionFactory actionFactory,
                                   @Nonnull DBMappingUpdateHandler dbMappingUpdateHandler) throws DBMappingAlreadyExistException {

        UpdateDBMappingAction action = actionFactory.createAction(projectId,
                newDBMapping, oldDBMapping);
        dispatchServiceManager.execute(action,
                result -> dbMappingUpdateHandler.handleDBMappingUpdated(result.getNewDBMapping(), result.getOldDBMapping(), result.getCode()));
    }

    public interface ActionFactory {


        UpdateDBMappingAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String newDBMapping,
                @Nonnull String oldDBMapping);
    }


    public interface DBMappingUpdateHandler {
        void handleDBMappingUpdated(NohrDBMapping newDBMapping, NohrDBMapping oldDBMapping, NohrResponseCodes code);
    }
}
