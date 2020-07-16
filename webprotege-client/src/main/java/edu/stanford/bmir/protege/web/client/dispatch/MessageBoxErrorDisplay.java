package edu.stanford.bmir.protege.web.client.dispatch;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.InvocationException;
import com.google.gwt.user.client.rpc.StatusCodeException;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.msgbox.MessageBox;
import edu.stanford.bmir.protege.web.shared.app.StatusCodes;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/02/15
 */
public class MessageBoxErrorDisplay implements DispatchErrorMessageDisplay {

    private static boolean displayingError = false;

    private final Messages messages;

    private final MessageBox messageBox;

    @Inject
    public MessageBoxErrorDisplay(@Nonnull Messages messages,
                                  @Nonnull MessageBox messageBox) {
        this.messages = checkNotNull(messages);
        this.messageBox = checkNotNull(messageBox);
    }


    private void displayMessageBox(String mainMessage, String subMessage) {
        GWT.log("[MessageBoxErrorDisplay] Display error. Disp: " + displayingError + " Msg: " + mainMessage);
        if (displayingError) {
            GWT.log("[MessageBoxErrorDisplay] " +
                            "Not displaying error message because an error message is already being displayed.  " +
                            "MainMessage: " + mainMessage);
            return;
        }
        displayingError = true;
        messageBox.showAlert(mainMessage, subMessage, () -> displayingError = false);
    }

    @Override
    public void displayPermissionDeniedErrorMessage() {
        displayPermissionDeniedErrorMessage(messages.error_permissionError_msg());
    }

    @Override
    public void displayPermissionDeniedErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_permissionError_title() , specificMessage);
    }

    //ADDED----------------------
    @Override
    public void displayRuleAlreadyExistErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_duplicatedRuleError_title() , specificMessage);
    }

    @Override
    public void displayDBMappingAlreadyExistErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_duplicatedDBMappingError_title() , specificMessage);
    }
    @Override
    public void displayEmptyFieldsErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_emptyFieldsError_title() , specificMessage);
    }

    @Override
    public void displayRuleParserErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_ruleParserError_title() , specificMessage);
    }

    @Override
    public void displayDatabaseErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_databaseError_title() , specificMessage);
    }

    @Override
    public void displayQueryParserErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_queryParserError_title() , specificMessage);
    }

    @Override
    public void displayUnsupportedAxiomsErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_unsupportedAxiomsError_title() , specificMessage);
    }

    @Override
    public void displayPrologEngineCreationErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_prologEngineCreation_title() , specificMessage);
    }

    @Override
    public void displayInconsistentOntologyErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_inconsistentOntology_title() , specificMessage);
    }

    @Override
    public void displayQueryExecutingErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_queryExecuting_title() , specificMessage);
    }

    @Override
    public void displayInvalidFileErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_invalidFile_title() , specificMessage);
    }

    @Override
    public void displayLockedTableErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_invalidAction_title() , specificMessage);
    }

    @Override
    public void displayNoTablesAddedErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_noTablesAdded_title() , specificMessage);
    }

    @Override
    public void displayUnknownErrorMessage(String specificMessage) {
        displayMessageBox(messages.error_unknownError_title() , specificMessage);
    }
    //ADDED----------------------

    @Override
    public void displayGeneralErrorMessage(String title, String message) {
        displayMessageBox(title, message);
    }

    @Override
    public void displayIncompatibleRemoteServiceExceptionErrorMessage() {
        displayReloadBrowserMessage();
    }

    private void displayReloadBrowserMessage() {
        displayMessageBox(
                messages.error_refreshBrowser() ,
                messages.error_upgraded());
    }

    @Override
    public void displayInvocationExceptionErrorMessage(InvocationException exception) {
        GWT.log("InvocationException: " + exception.getClass().getName() + ": " + exception.getMessage(), exception);
        if (exception instanceof StatusCodeException) {
            StatusCodeException statusCodeException = (StatusCodeException) exception;
            if(statusCodeException.getStatusCode() == StatusCodes.UPDATED) {
                displayReloadBrowserMessage();
            }
            else if (statusCodeException.getStatusCode() != 0) {
                displayMessageBox(statusCodeException.getStatusText(),
                                  messages.error_webProtegeHasEncounteredAnErrorPleaseTryAgain() + "<br>" +
                                  messages.error_statusCode() + ": " + statusCodeException.getStatusCode() + " (" + statusCodeException
                                          .getStatusText() + ")"
                );
            }
            else {
                displayMessageBox(messages.error_connectionError_title() ,
                                  messages.error_connectionError_msg());
            }
        }
        else {
            displayMessageBox(
                    messages.error() ,
                    messages.error_general() );
        }

    }
}
