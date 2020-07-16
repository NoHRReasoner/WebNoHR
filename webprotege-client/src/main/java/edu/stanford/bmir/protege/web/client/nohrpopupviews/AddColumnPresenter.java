package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectManager;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
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
public class AddColumnPresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final AddColumnDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final ActiveProjectManager activeProjectManager;

    @Nonnull
    private final DisplayNameSettingsManager displayNameSettingsManager;

    @Nonnull
    private final Messages messages;

    private Optional<String> currentLangTag = Optional.empty();

    @Inject
    public AddColumnPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                              @Nonnull ProjectId projectId,
                              @Nonnull AddColumnDialogView view,
                              @Nonnull ModalManager modalManager,
                              @Nonnull ActiveProjectManager activeProjectManager,
                              @Nonnull DisplayNameSettingsManager displayNameSettingsManager,
                              @Nonnull Messages messages) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.activeProjectManager = checkNotNull(activeProjectManager);
        this.displayNameSettingsManager = checkNotNull(displayNameSettingsManager);
        this.messages = checkNotNull(messages);

    }

    public void createRows(@Nonnull RowsCreatedHandler rowsCreatedHandler, List<String> tables) {
        view.clear();
        view.setTablesInListBox(tables);
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Add a new Column");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleCreateRows(view.getTable(), view.getColumnText(), view.getIsFloating(), rowsCreatedHandler);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }


    public void updateRows(@Nonnull RowsUpdatedHandler rowsUpdatedHandler, NohrColumnsTable oldRow, List<String> tables, Integer elementIndex) {
        view.clear();
        view.setTablesInListBox(tables);
        view.setRowElements(oldRow, elementIndex);
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Edit Column");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUpdateRows(view.getTable(), view.getColumnText(), view.getIsFloating(),oldRow, rowsUpdatedHandler);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }

    private void handleCreateRows(@Nonnull String tableText,
                                  @Nonnull String columnNameText,
                                  @Nonnull Boolean isFloating,
                                  @Nonnull RowsCreatedHandler rowsCreatedHandler) {

        NohrColumnsTable newRow = new NohrColumnsTable(tableText,columnNameText,isFloating);

        String[] splitNameAndNumber = tableText.split(" as t");
        newRow.setTableCol(splitNameAndNumber[0]);
        newRow.setNumber(Integer.parseInt(splitNameAndNumber[1]));

        rowsCreatedHandler.handleRowsCreated(newRow);


    }

    private void handleUpdateRows(@Nonnull String tableText,
                                  @Nonnull String columnNameText,
                                  @Nonnull Boolean isFloating,
                                  @Nonnull NohrColumnsTable oldRow,
                                  @Nonnull RowsUpdatedHandler rowsUpdatedHandler) {

        NohrColumnsTable newRow = new NohrColumnsTable(tableText,columnNameText,isFloating);

        String[] splitNameAndNumber = tableText.split(" as t");
        newRow.setTableCol(splitNameAndNumber[0]);
        newRow.setNumber(Integer.parseInt(splitNameAndNumber[1]));

        rowsUpdatedHandler.handleRowsUpdated(oldRow, newRow);


    }

    public interface RowsCreatedHandler {
        void handleRowsCreated(NohrColumnsTable row);
    }

    public interface RowsUpdatedHandler {
        void handleRowsUpdated(NohrColumnsTable oldRow, NohrColumnsTable newRow);
    }
}
