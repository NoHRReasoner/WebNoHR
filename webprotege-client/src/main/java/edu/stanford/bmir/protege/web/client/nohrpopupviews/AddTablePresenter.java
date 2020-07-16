package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectManager;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
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
public class AddTablePresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final AddTableDialogView view;

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
    public AddTablePresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                             @Nonnull ProjectId projectId,
                             @Nonnull AddTableDialogView view,
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

    public void createRows(@Nonnull TablesCreatedHandler tablesCreatedHandler, List<String> tables, Boolean isFirstElement) {
        view.clear();
        view.setTablesInListBox(tables);
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Add a new Table");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleCreateTables(view.getTable(), view.getColumn(), view.getJoinTable(), view.getOnColumn(), tablesCreatedHandler, isFirstElement);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }

    public void updateRows(@Nonnull TablesUpdateHandler tablesCreatedHandler, NohrTablesTable oldRow, List<String> tables, Boolean isFirstElement, Integer elementIndex) {
        view.clear();
        view.setTablesInListBox(tables);
        view.setRowElements(oldRow, elementIndex);
        /*view.setJoinTableItem(elementIndex);*/
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle("Edit Table");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);
        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUpdateTables(view.getTable(), view.getColumn(), view.getJoinTable(), view.getOnColumn(), oldRow, tablesCreatedHandler, isFirstElement);
            closer.closeModal();
        });
        modalManager.showModal(modalPresenter);

    }

    public void disableElements() {
        view.disableElements();
    }

    public void enableElements() {
        view.enableElements();
    }


    private void handleCreateTables(@Nonnull String table,
                                    @Nonnull String column,
                                    @Nonnull String joinWith,
                                    @Nonnull String onColumn,
                                    @Nonnull TablesCreatedHandler tablesCreatedHandler,
                                    @Nonnull Boolean isFirstElement) {

        NohrTablesTable row = new NohrTablesTable(table, column, joinWith, onColumn);
        if (!isFirstElement) {
            String[] splitNameAndNumber = joinWith.split(" as t");
            row.setJoinTable(splitNameAndNumber[0]);
            row.setJoinNumber(Integer.parseInt(splitNameAndNumber[1]));
        }
        tablesCreatedHandler.handleTablesCreated(row);
    }

    private void handleUpdateTables(@Nonnull String table,
                                    @Nonnull String column,
                                    @Nonnull String joinWith,
                                    @Nonnull String onColumn,
                                    @Nonnull NohrTablesTable oldRow,
                                    @Nonnull TablesUpdateHandler tablesUpdateHandler,
                                    @Nonnull Boolean isFirstElement) {

        NohrTablesTable newRow = new NohrTablesTable(table, column, joinWith, onColumn);
        if (!isFirstElement) {
            String[] splitNameAndNumber = joinWith.split(" as t");
            newRow.setTableNumber(oldRow.getNumber());
            newRow.setJoinTable(splitNameAndNumber[0]);
            newRow.setJoinNumber(Integer.parseInt(splitNameAndNumber[1]));
        } else {
            newRow.setTableNumber(1);
        }
        tablesUpdateHandler.handleTablesUpdated(oldRow, newRow);
    }


    public interface TablesCreatedHandler {
        void handleTablesCreated(NohrTablesTable row);
    }

    public interface TablesUpdateHandler {
        void handleTablesUpdated(NohrTablesTable oldRow, NohrTablesTable newRow);
    }
}
