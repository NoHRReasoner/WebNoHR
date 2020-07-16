package edu.stanford.bmir.protege.web.client.nohrrules;

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
import edu.stanford.bmir.protege.web.client.nohrpopupviews.CreateRulePresenter;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UpdateRulePresenter;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UploadRulesPresenter;
import edu.stanford.bmir.protege.web.client.permissions.LoggedInUserProjectPermissionChecker;
import edu.stanford.bmir.protege.web.client.portlet.HasPortletActions;
import edu.stanford.bmir.protege.web.client.portlet.PortletAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.*;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
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
public class NoHRRulePresenter {

    private final DispatchServiceManager dsm;

    private final Messages messages;

    private final NoHRRuleView view;

    private final UIAction createAction;

    private final UIAction deleteAction;

    private final UIAction downloadAction;

    private final UIAction uploadAction;

    @Nonnull
    private final ProjectId projectId;

    private final LoggedInUserProjectPermissionChecker permissionChecker;

    @Nonnull
    private final CreateRulePresenter createRulePresenter;

    @Nonnull
    private final UpdateRulePresenter updateRulePresenter;

    @Nonnull
    private final UploadRulesPresenter uploadRulesPresenter;

    private MessageBox messageBox;

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Nonnull
    private final NohrRulesProvider nohrRulesProvider;

    @Inject
    public NoHRRulePresenter(NoHRRuleView view,
                             @Nonnull ProjectId projectId,
                             DispatchServiceManager dispatchServiceManager,
                             LoggedInUserProjectPermissionChecker permissionChecker,
                             Messages messages,
                             @Nonnull CreateRulePresenter createRulePresenter,
                             @Nonnull UpdateRulePresenter updateRulePresenter,
                             @Nonnull UploadRulesPresenter uploadRulesPresenter,
                             MessageBox messageBox,
                             DispatchErrorMessageDisplay errorMessageDisplay,
                             NohrRulesProvider nohrRulesProvider) {
        this.projectId = projectId;
        this.permissionChecker = permissionChecker;
        this.view = view;
        this.dsm = dispatchServiceManager;
        this.messages = messages;
        this.createRulePresenter = createRulePresenter;
        this.updateRulePresenter = updateRulePresenter;
        this.uploadRulesPresenter = uploadRulesPresenter;
        this.messageBox = messageBox;
        this.errorMessageDisplay = errorMessageDisplay;
        this.nohrRulesProvider = nohrRulesProvider;
        this.view.addSelectionHandler(this::handleSelectionChangedInView);
        this.view.addDoubleClickHandler(this::handleDoubleClickInView);
        this.createAction = new PortletAction(messages.create(), "wp-btn-g--create-NoHR-rule wp-btn-g--create", this::handleCreateNoHRRule);
        this.deleteAction = new PortletAction(messages.delete(), "wp-btn-g--delete-NoHR-rule wp-btn-g--delete", this::handleDeleteNoHRRule);
        this.downloadAction = new PortletAction(messages.nohr_download(), "wp-btn-g--download-NoHR-rule wp-btn-g--download", this::handleDownloadNoHRRule);
        this.uploadAction = new PortletAction(messages.nohr_upload(), "wp-btn-g--upload-NoHR-rule wp-btn-g--upload", this::handleUploadNoHRRule);
    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        GWT.log("[NoHRQueryPresenter] Started NoHR Rule");
        GetRulesAction action = new GetRulesAction(projectId);
        dsm.execute(action
                , result -> handleGetRules(result.getRules(), result.getCode()));
        container.setWidget(view.asWidget());
    }

    private void handleCreateNoHRRule() {
        createRulePresenter.createRules(this::handleNoHRRuleCreated,
                (projectId, createFromText)
                        -> new CreateRulesAction(projectId,
                        createFromText));
    }

    private void handleNoHRRuleCreated(NohrRule rule, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NohrRule r : view.getListData())
                    if (r.getRule().equals(rule.getRule())) {
                        errorMessageDisplay.displayRuleAlreadyExistErrorMessage("Rule already defined in this project");
                        return;
                    }
                for (NoHRRulePresenter view : nohrRulesProvider.getViews()) {
                    view.view.addListData(rule);
                }
                view.setSelectedRule(rule);
                break;
            case PARSER_ERROR:
                errorMessageDisplay.displayRuleParserErrorMessage("Rule syntax is not correct");
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Cannot connect to database");
                break;
        }
    }

    private void handleDeleteNoHRRule() {
        Collection<NohrRule> sel = view.getSelectedRules();
        if (sel.isEmpty()) {
            return;
        }
        String subMessage;
        String title;
        if (sel.size() == 1) {
            String browserText = sel.iterator().next().getRule();
            title = messages.delete_entity_title(browserText);
            subMessage = messages.delete_entity_msg("Rule", browserText);
        } else {
            title = messages.delete_entity_title("Rules");
            subMessage = "Are you sure you want to delete " + sel.size() + " Rules?";
        }
        messageBox.showConfirmBox(title,
                subMessage,
                CANCEL, DELETE,
                this::deleteSelectedRules,
                CANCEL);
    }

    private void deleteSelectedRules() {
        Collection<NohrRule> selection = view.getSelectedRules();
        dsm.execute(new DeleteRulesAction(projectId, selection),
                view,
                result -> deleteRulesFromView(result.getRules(), result.getCode()));
    }

    private void deleteRulesFromView(Collection<NohrRule> rules, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NoHRRulePresenter view : nohrRulesProvider.getViews()) {
                    view.view.removeListData(rules);
                }
                break;
            case DATABASE_ERROR:
                errorMessageDisplay.displayDatabaseErrorMessage("Cannot connect to database");
                break;
        }
    }

    private void handleUploadNoHRRule() {
        uploadAction.setEnabled(false);
        uploadRulesPresenter.uploadRules(this::handleNoHRRuleUploaded,
                (projectId, ruleFile)
                        -> new UploadRulesAction(projectId,
                        ruleFile));
        uploadAction.setEnabled(true);
    }

    private void handleNoHRRuleUploaded(List<NohrRule> rules, int lineNumber, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NoHRRulePresenter n : nohrRulesProvider.getViews())
                    n.view.setListData(rules);
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
                errorMessageDisplay.displayRuleParserErrorMessage("Invalid rule inside file on line " + lineNumber);
                break;
            case UNKNOWN_ERROR:
                errorMessageDisplay.displayInvalidFileErrorMessage("Unknown Error");
                break;
        }
    }

    private void handleDownloadNoHRRule() {
        downloadAction.setEnabled(false);
        String encodedProjectName = URL.encode(projectId.getId());
        String baseURL = GWT.getHostPageBaseURL();
        String downloadURL = baseURL + "download" + "/"
                + "downloadrules?" + PROJECT + "=" + encodedProjectName;
        Window.open(downloadURL, "Download rules", "");
        downloadAction.setEnabled(true);
    }

    /*private void updateList() {
        GetRulesAction action = new GetRulesAction(projectId);
        dsm.execute(action
                , result -> handleGetRules(result.getRules(), result.getCode()));
    }*/

    private void handleGetRules(List<NohrRule> rules, NohrResponseCodes code) {
        switch (code) {
            case OK:
                view.setListData(rules);
                break;
        }
    }

    private void handleSelectionChangedInView(SelectionEvent<List<NohrRule>> event) {
/*
        event.getSelectedItem().stream().findFirst().ifPresent(sel -> selectionModel.setSelection(sel.getRule()));
*/
    }

    private void handleDoubleClickInView(DoubleClickEvent event) {
        updateRulePresenter.updateRules(this::handleNoHRRuleUpdated,
                view.getSelectedRule().get().getRule(),
                (projectId, newRule, oldRule)
                        -> new UpdateRulesAction(projectId,
                        newRule, oldRule));
    }

    private void handleNoHRRuleUpdated(NohrRule newRule, NohrRule oldRule, NohrResponseCodes code) {
        switch (code) {
            case OK:
                for (NohrRule r : view.getListData())
                    if (r.getRule().equals(newRule.getRule())) {
                        errorMessageDisplay.displayRuleAlreadyExistErrorMessage("Rule already defined in this project");
                        return;
                    }
                for (NoHRRulePresenter n : nohrRulesProvider.getViews()) {
                    n.view.updateListData(newRule, oldRule);
                }
                view.setSelectedRule(newRule);
                break;
            case PARSER_ERROR:
                errorMessageDisplay.displayRuleParserErrorMessage("Rule syntax is not correct");
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
        permissionChecker.hasPermission(CREATE_RULE, createAction::setEnabled);
        permissionChecker.hasPermission(DELETE_RULE, deleteAction::setEnabled);
        permissionChecker.hasPermission(DOWNLOAD_RULE, downloadAction::setEnabled);
        permissionChecker.hasPermission(UPLOAD_RULE, uploadAction::setEnabled);


    }
}
