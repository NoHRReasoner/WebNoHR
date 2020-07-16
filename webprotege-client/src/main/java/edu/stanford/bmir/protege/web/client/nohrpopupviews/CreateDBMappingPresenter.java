package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.library.dlg.DialogButton;
import edu.stanford.bmir.protege.web.client.library.modal.ModalCloser;
import edu.stanford.bmir.protege.web.client.library.modal.ModalManager;
import edu.stanford.bmir.protege.web.client.library.modal.ModalPresenter;
import edu.stanford.bmir.protege.web.client.project.ActiveProjectManager;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.CreateDBMappingAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingInfoAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingSettingsAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateDBMappingAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.DBMappingAlreadyExistException;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
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
public class CreateDBMappingPresenter {

    @Nonnull
    private final DispatchServiceManager dispatchServiceManager;

    @Nonnull
    private final ProjectId projectId;

    @Nonnull
    private final CreateDBMappingDialogView view;

    @Nonnull
    private final ModalManager modalManager;

    @Nonnull
    private final ActiveProjectManager activeProjectManager;

    @Nonnull
    private final DisplayNameSettingsManager displayNameSettingsManager;

    @Nonnull
    private final Messages messages;

    @Nonnull
    private DispatchErrorMessageDisplay errorMessageDisplay;

    private Optional<String> currentLangTag = Optional.empty();

    @Inject
    public CreateDBMappingPresenter(@Nonnull DispatchServiceManager dispatchServiceManager,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull CreateDBMappingDialogView view,
                                    @Nonnull ModalManager modalManager,
                                    @Nonnull ActiveProjectManager activeProjectManager,
                                    @Nonnull DisplayNameSettingsManager displayNameSettingsManager,
                                    @Nonnull Messages messages,
                                    DispatchErrorMessageDisplay errorMessageDisplay) {
        this.dispatchServiceManager = checkNotNull(dispatchServiceManager);
        this.projectId = checkNotNull(projectId);
        this.view = view;
        this.modalManager = modalManager;
        this.activeProjectManager = checkNotNull(activeProjectManager);
        this.displayNameSettingsManager = checkNotNull(displayNameSettingsManager);
        this.messages = checkNotNull(messages);
        this.errorMessageDisplay = checkNotNull(errorMessageDisplay);

    }

    public void createDBMapping(@Nonnull DBMappingCreatedHandler dbMappingCreatedHandler,
                                @Nonnull ActionCreateFactory actionCreateFactory) {
        view.clear();
        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle(messages.create() + " ");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);

        GetDBMappingSettingsAction action = new GetDBMappingSettingsAction(projectId);
        dispatchServiceManager.execute(action
                , result -> handleGetDBMappingsSettings(result.getDbMappings(), result.getCode()));


        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleCreateDBMapping(view.getODBC(), view.getPredicateText(), view.getArityNumber(), view.getSQLText(), actionCreateFactory, dbMappingCreatedHandler, closer);
        });

        modalManager.showModal(modalPresenter);
    }

    public void updateDBMapping(@Nonnull DBMappingUpdatedHandler dbMappingUpdatedHandler,
                                @Nonnull String oldDBMapping,
                                @Nonnull ActionUpdateFactory actionFactory) {
        view.clear();

        ModalPresenter modalPresenter = modalManager.createPresenter();
        modalPresenter.setTitle(messages.update() + " ");
        modalPresenter.setView(view);
        modalPresenter.setEscapeButton(DialogButton.CANCEL);
        modalPresenter.setPrimaryButton(DialogButton.CREATE);

        GetDBMappingSettingsAction action = new GetDBMappingSettingsAction(projectId);
        dispatchServiceManager.execute(action
                , result -> handleGetDBMappingsSettings(result.getDbMappings(), result.getCode()));

        GetDBMappingInfoAction getAction = new GetDBMappingInfoAction(projectId, oldDBMapping);
        dispatchServiceManager.execute(getAction
                , result -> handleGetDBMappingsInfo(
                        result.getOdbcDriver(),
                        result.getSelectColumnsList(),
                        result.getFromTablesList(),
                        result.getPredicate(),
                        result.getArity(),
                        result.getSql(),
                        result.getCode()));

        modalPresenter.setButtonHandler(DialogButton.CREATE, closer -> {
            handleUpdateDBMapping(view.getODBC(), view.getPredicateText(), view.getArityNumber(), view.getSQLText(), actionFactory, dbMappingUpdatedHandler, oldDBMapping, closer);
        });

        modalManager.showModal(modalPresenter);

    }

    private void handleGetDBMappingsInfo(String odbcDriver, List<NohrColumnsTable> selectColumnsList, List<NohrTablesTable> fromTablesList, String predicate, Integer arity, String sql, NohrResponseCodes code) {
        view.setInfo(odbcDriver,selectColumnsList,fromTablesList,predicate,arity,sql);
    }

    private void handleGetDBMappingsSettings(List<NohrDatabaseSettings> databaseSettings, NohrResponseCodes code) {
        switch (code) {
            case OK:
                view.setDatabaseSettings(databaseSettings);
                break;
        }
    }

    private void handleCreateDBMapping(@Nonnull String odbc,
                                       @Nonnull String predicate,
                                       @Nonnull Integer arity,
                                       @Nonnull String sql,
                                       @Nonnull ActionCreateFactory actionCreateFactory,
                                       @Nonnull DBMappingCreatedHandler dbMappingCreatedHandler,
                                       @Nonnull ModalCloser closer) throws DBMappingAlreadyExistException {

        if (!view.isValidInput()) {
            errorMessageDisplay.displayEmptyFieldsErrorMessage("Please fill the fields! You should select at least one column from a table or write your own SQL");
            return;
        }

        CreateDBMappingAction action;
        if (view.autoSQLWriting()) {

            int numCols = view.getSelectColumnsList().size();
            NohrColumnsTable[] nohrSelectColumns = new NohrColumnsTable[numCols];
            for (int i = 0; i < numCols; i++) {
                nohrSelectColumns[i] = view.getSelectColumnsList().get(i);
            }

            int numTables = view.getFromTablesList().size();
            NohrTablesTable[] nohrFromTables = new NohrTablesTable[numTables];
            for (int i = 0; i < numTables; i++) {
                nohrFromTables[i] = view.getFromTablesList().get(i);
            }

            action = actionCreateFactory.createAction(projectId,
                    odbc, nohrSelectColumns, nohrFromTables, predicate, arity, sql);
        } else {
            action = actionCreateFactory.createAction(projectId,
                    odbc, new NohrColumnsTable[0], new NohrTablesTable[0], predicate, arity, sql);
        }
        dispatchServiceManager.execute(action,
                result -> dbMappingCreatedHandler.handleDBMappingCreated(result.getDbMapping(), result.getUIDbMapping(), result.getCode()));
        closer.closeModal();
    }

    private void handleUpdateDBMapping(@Nonnull String odbc,
                                       @Nonnull String predicate,
                                       @Nonnull Integer arity,
                                       @Nonnull String sql,
                                       @Nonnull ActionUpdateFactory actionFactory,
                                       @Nonnull DBMappingUpdatedHandler dbMappingUpdatedHandler,
                                       @Nonnull String oldDBMapping,
                                       @Nonnull ModalCloser closer) throws DBMappingAlreadyExistException {

        if (!view.isValidInput()) {
            errorMessageDisplay.displayEmptyFieldsErrorMessage("Please fill the fields! You should select at least one column from a table or write your own SQL");
            return;
        }

        UpdateDBMappingAction action;
        if (view.autoSQLWriting()) {

            int numCols = view.getSelectColumnsList().size();
            NohrColumnsTable[] nohrSelectColumns = new NohrColumnsTable[numCols];
            for (int i = 0; i < numCols; i++) {
                nohrSelectColumns[i] = view.getSelectColumnsList().get(i);
            }

            int numTables = view.getFromTablesList().size();
            NohrTablesTable[] nohrFromTables = new NohrTablesTable[numTables];
            for (int i = 0; i < numTables; i++) {
                nohrFromTables[i] = view.getFromTablesList().get(i);
            }

            action = actionFactory.updateAction(projectId,
                    odbc, nohrSelectColumns, nohrFromTables, predicate, arity, sql, oldDBMapping);
        } else {
            action = actionFactory.updateAction(projectId,
                    odbc, new NohrColumnsTable[0], new NohrTablesTable[0], predicate, arity, sql, oldDBMapping);
        }
        dispatchServiceManager.execute(action,
                result -> dbMappingUpdatedHandler.handleDBMappingUpdated(result.getNewDBMapping(), result.getOldDBMapping(), result.getNewUIMapping(), result.getOldUIMapping(), result.getCode()));
        closer.closeModal();
    }


    public interface ActionCreateFactory {

        CreateDBMappingAction createAction(
                @Nonnull ProjectId projectId,
                @Nonnull String odbc,
                @Nonnull NohrColumnsTable[] selectColumnsList,
                @Nonnull NohrTablesTable[] fromTablesList,
                @Nonnull String predicate,
                @Nonnull Integer arity,
                @Nonnull String sql);

    }

    public interface ActionUpdateFactory {

        UpdateDBMappingAction updateAction(
                @Nonnull ProjectId projectId,
                @Nonnull String odbc,
                @Nonnull NohrColumnsTable[] selectColumnsList,
                @Nonnull NohrTablesTable[] fromTablesList,
                @Nonnull String predicate,
                @Nonnull Integer arity,
                @Nonnull String sql,
                @Nonnull String oldDBMapping);
    }


    public interface DBMappingCreatedHandler {
        void handleDBMappingCreated(NohrDBMapping dbMapping, NohrDBMapping uiDBMapping, NohrResponseCodes code);
    }

    public interface DBMappingUpdatedHandler {
        void handleDBMappingUpdated(NohrDBMapping newDBMapping,
                                    NohrDBMapping oldDBMapping,
                                    NohrDBMapping newUIMapping,
                                    NohrDBMapping oldUIMapping,
                                    NohrResponseCodes code);
    }
}
