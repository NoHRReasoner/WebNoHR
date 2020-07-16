package edu.stanford.bmir.protege.web.client.nohrdbmappings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.action.UIAction;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.CreateDBMappingPresenter;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UpdateDBMappingPresenter;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UploadDBMappingPresenter;
import edu.stanford.bmir.protege.web.client.permissions.LoggedInUserProjectPermissionChecker;
import edu.stanford.bmir.protege.web.client.portlet.HasPortletActions;
import edu.stanford.bmir.protege.web.client.portlet.PortletAction;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMappingImpl;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static edu.stanford.bmir.protege.web.client.library.dlg.DialogButton.CANCEL;
import static edu.stanford.bmir.protege.web.client.library.dlg.DialogButton.DELETE;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.*;
import static edu.stanford.bmir.protege.web.shared.download.ProjectDownloadConstants.PROJECT;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NoHRDBMappingsPresenter {

    private final DispatchServiceManager dsm;

    private final Messages messages;

    private final NoHRDBMappingsView view;

    private final UIAction createAction;

    private final UIAction deleteAction;

    private final UIAction downloadAction;

    private final UIAction uploadAction;

    @Nonnull
    private final ProjectId projectId;

    private final LoggedInUserProjectPermissionChecker permissionChecker;

    @Nonnull
    private final CreateDBMappingPresenter createDBMappingPresenter;

    @Nonnull
    private final UpdateDBMappingPresenter updateDBMappingPresenter;

    @Nonnull
    private final UploadDBMappingPresenter uploadDBMappingPresenter;

    private MessageBox messageBox;

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Nonnull
    private final NohrDBMappingsProvider nohrDBMappingsProvider;

    @Inject
    public NoHRDBMappingsPresenter(NoHRDBMappingsView view,
                                   @Nonnull ProjectId projectId,
                                   DispatchServiceManager dispatchServiceManager,
                                   LoggedInUserProjectPermissionChecker permissionChecker,
                                   Messages messages,
                                   @Nonnull CreateDBMappingPresenter createDBMappingPresenter,
                                   @Nonnull UpdateDBMappingPresenter updateDBMappingPresenter,
                                   @Nonnull UploadDBMappingPresenter uploadDBMappingPresenter,
                                   MessageBox messageBox,
                                   DispatchErrorMessageDisplay errorMessageDisplay,
                                   NohrDBMappingsProvider nohrDBMappingsProvider) {
        this.projectId = projectId;
        this.permissionChecker = permissionChecker;
        this.view = view;
        this.dsm = dispatchServiceManager;
        this.messages = messages;
        this.createDBMappingPresenter = createDBMappingPresenter;
        this.updateDBMappingPresenter = updateDBMappingPresenter;
        this.uploadDBMappingPresenter = uploadDBMappingPresenter;
        this.messageBox = messageBox;
        this.errorMessageDisplay = errorMessageDisplay;
        this.nohrDBMappingsProvider = nohrDBMappingsProvider;
        this.view.addSelectionHandler(this::handleSelectionChangedInView);
        this.view.addDoubleClickHandler(this::handleDoubleClickInView);
        this.createAction = new PortletAction(messages.create(), "wp-btn-g--create-NoHR-rule wp-btn-g--create", this::handleCreateNoHRDBMapping);
        this.deleteAction = new PortletAction(messages.delete(), "wp-btn-g--delete-NoHR-rule wp-btn-g--delete", this::handleDeleteNoHRDBMappings);
        this.downloadAction = new PortletAction(messages.nohr_download(), "wp-btn-g--download-NoHR-rule wp-btn-g--download", this::handleDownloadNoHRDBMappings);
        this.uploadAction = new PortletAction(messages.nohr_upload(), "wp-btn-g--upload-NoHR-rule wp-btn-g--upload", this::handleUploadNoHRDBMapping);
    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        GWT.log("[NoHRQueryPresenter] Started NoHR Database Mappings");
        GetDBMappingAction action = new GetDBMappingAction(projectId);
        dsm.execute(action
                , result -> handleGetDBMappings(result.getDbMappings(), result.getUiMappings(), result.getCode()));
        container.setWidget(view.asWidget());
    }

    private void handleCreateNoHRDBMapping() {
        createDBMappingPresenter.createDBMapping(this::handleNoHRDBMappingCreated,
                (projectId, odbc, columnsList, tablesList, predicate, arity, sql)
                        -> new CreateDBMappingAction(projectId, odbc, columnsList, tablesList,
                        predicate, arity, sql));
    }

    private void handleNoHRDBMappingCreated(NohrDBMapping dbMapping, NohrDBMapping uiDBMapping, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NohrDBMapping r : view.getListData())
                    if (r.getDbMapping().equals(dbMapping.getDbMapping())) {
                        errorMessageDisplay.displayDBMappingAlreadyExistErrorMessage("Database Mapping already defined in this project");
                        return;
                    }
                nohrDBMappingsProvider.insertUiMapping(uiDBMapping.getDbMapping(), dbMapping.getDbMapping());
                for (NoHRDBMappingsPresenter view : nohrDBMappingsProvider.getViews()) {
                    view.view.addListData(uiDBMapping);
                }
                view.setSelectedDBMapping(uiDBMapping);
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Cannot connect to database");
                break;
            case UNKNOWN_ERROR:
                errorMessageDisplay.displayUnknownErrorMessage("Cannot create a database mapping");
                break;
        }
    }

    private void handleDeleteNoHRDBMappings() {
        Collection<NohrDBMapping> sel = view.getSelectedDBMappings();
        if (sel.isEmpty()) {
            return;
        }
        String subMessage;
        String title;
        if (sel.size() == 1) {
            String browserText = sel.iterator().next().getDbMapping();
            title = messages.delete_entity_title(browserText);
            subMessage = messages.delete_entity_msg("Mapping", browserText);
        } else {
            title = messages.delete_entity_title("Mappings");
            subMessage = "Are you sure you want to delete " + sel.size() + " Mappings?";
        }
        messageBox.showConfirmBox(title,
                subMessage,
                CANCEL, DELETE,
                this::deleteSelectedDBMappings,
                CANCEL);
    }

    private void deleteSelectedDBMappings() {
        Collection<NohrDBMapping> uiSelection = view.getSelectedDBMappings();
        List<NohrDBMapping> dbSelection = new LinkedList<>();
        for (NohrDBMapping sel : uiSelection) {
            dbSelection.add(new NohrDBMappingImpl(nohrDBMappingsProvider.getDBFormatMapping(sel.getDbMapping())));
        }

        dsm.execute(new DeleteDBMappingAction(projectId, dbSelection, uiSelection),
                view,
                result -> deleteDBMappingsFromView(result.getUiMappings(), result.getCode()));
    }

    private void deleteDBMappingsFromView(Collection<NohrDBMapping> uiMappings, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NoHRDBMappingsPresenter view : nohrDBMappingsProvider.getViews()) {
                    view.view.removeListData(uiMappings);
                }
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Cannot connect to database");
                break;
        }
    }

    private void handleUploadNoHRDBMapping() {
        uploadAction.setEnabled(false);
        uploadDBMappingPresenter.uploadDBMapping(this::handleNoHRDBMappingsUploaded,
                (projectId, dbMappingFile)
                        -> new UploadDBMappingAction(projectId,
                        dbMappingFile));
        uploadAction.setEnabled(true);
    }

    private void handleNoHRDBMappingsUploaded(List<NohrDBMapping> dbMappings, List<NohrDBMapping> uiMappings, Integer line, NohrResponseCodes code) {
        switch (code) {
            case OK:
                nohrDBMappingsProvider.clearMappings();
                for (int i = 0; i < dbMappings.size(); i++)
                    nohrDBMappingsProvider.insertUiMapping(uiMappings.get(i).getDbMapping(), dbMappings.get(i).getDbMapping());

                for (NoHRDBMappingsPresenter n : nohrDBMappingsProvider.getViews())
                    n.view.setListData(uiMappings);
                break;
            case INVALIDFILE_ERROR:
                errorMessageDisplay.displayInvalidFileErrorMessage("Invalid File");
                break;
            case NONFILE_ERROR:
                errorMessageDisplay.displayInvalidFileErrorMessage("No File Selected");
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Database Error");
                break;
            case PARSER_ERROR:
                errorMessageDisplay.displayRuleParserErrorMessage("Invalid Database Mapping Inside File in line "+line);
                break;
            case UNKNOWN_ERROR:
                errorMessageDisplay.displayInvalidFileErrorMessage("Unknown Error");
                break;
        }
    }

    private void handleDownloadNoHRDBMappings() {
        downloadAction.setEnabled(false);
        String encodedProjectName = URL.encode(projectId.getId());
        String baseURL = GWT.getHostPageBaseURL();
        String downloadURL = baseURL + "download" + "/"
                + "downloaddbmapping?" + PROJECT + "=" + encodedProjectName;
        Window.open(downloadURL, "Download dbmappings", "");
        downloadAction.setEnabled(true);
    }

    /*private void updateList() {
        GetRulesAction action = new GetRulesAction(projectId);
        dsm.execute(action
                , result -> handleGetRules(result.getRules(), result.getCode()));
    }*/

    private void handleGetDBMappings(List<NohrDBMapping> dbMappings, List<NohrDBMapping> uiMappings, NohrResponseCodes code) {
        switch (code) {
            case OK:
                nohrDBMappingsProvider.clearMappings();
                for (int i = 0; i < dbMappings.size(); i++)
                    nohrDBMappingsProvider.insertUiMapping(uiMappings.get(i).getDbMapping(), dbMappings.get(i).getDbMapping());

                view.setListData(uiMappings);
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Cannot connect to database");
                break;
        }
    }

    private void handleSelectionChangedInView(SelectionEvent<List<NohrDBMapping>> event) {
/*
        event.getSelectedItem().stream().findFirst().ifPresent(sel -> selectionModel.setSelection(sel.getRule()));
*/
    }

    private void handleDoubleClickInView(DoubleClickEvent event) {
        String dbFormatMapping = nohrDBMappingsProvider.getDBFormatMapping(view.getSelectedDBMapping().get().getDbMapping());

        createDBMappingPresenter.updateDBMapping(this::handleNoHRDBMappingsUpdated,
                dbFormatMapping,
                (projectId, odbc, columnsList, tablesList, predicate, arity, sql, oldDBMapping)
                        -> new UpdateDBMappingAction(projectId, odbc, columnsList, tablesList, predicate, arity, sql, oldDBMapping));
    }

    private void handleNoHRDBMappingsUpdated(NohrDBMapping newDBMapping,
                                             NohrDBMapping oldDBMapping,
                                             NohrDBMapping newUIMapping,
                                             NohrDBMapping oldUIMapping,
                                             NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NohrDBMapping r : view.getListData()) {
                    if (newDBMapping.getDbMapping().equals(oldDBMapping.getDbMapping())) {
                        errorMessageDisplay.displayDBMappingAlreadyExistErrorMessage("Database Mapping already defined in this project");
                        return;
                    }
                }

                nohrDBMappingsProvider.updateUiMapping(oldUIMapping.getDbMapping(), newUIMapping.getDbMapping(), newDBMapping.getDbMapping());
                for (NoHRDBMappingsPresenter n : nohrDBMappingsProvider.getViews()) {
                    n.view.updateListData(newUIMapping, oldUIMapping);
                }
                view.setSelectedDBMapping(newUIMapping);
                break;
            case PARSER_ERROR:
                errorMessageDisplay.displayRuleParserErrorMessage("Database Mapping is not valid");
                break;
        }
    }


    public void installActions(HasPortletActions hasPortletActions) {
        hasPortletActions.addAction(createAction);
        hasPortletActions.addAction(deleteAction);
        hasPortletActions.addAction(downloadAction);
        hasPortletActions.addAction(uploadAction);
        updateButtonStates();
    }

    private void updateButtonStates() {
        createAction.setEnabled(false);
        deleteAction.setEnabled(false);
        downloadAction.setEnabled(false);
        uploadAction.setEnabled(false);
        permissionChecker.hasPermission(CREATE_DBMAPPING, createAction::setEnabled);
        permissionChecker.hasPermission(DELETE_DBMAPPING, deleteAction::setEnabled);
        permissionChecker.hasPermission(DOWNLOAD_DBMAPPING, downloadAction::setEnabled);
        permissionChecker.hasPermission(UPLOAD_DBMAPPING, uploadAction::setEnabled);


    }
}
