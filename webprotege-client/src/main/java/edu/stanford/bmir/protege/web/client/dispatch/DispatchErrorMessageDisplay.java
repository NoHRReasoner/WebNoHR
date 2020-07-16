package edu.stanford.bmir.protege.web.client.dispatch;

import com.google.gwt.user.client.rpc.InvocationException;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/02/15
 */
public interface DispatchErrorMessageDisplay {

    //ADDED----------------------
    void displayRuleAlreadyExistErrorMessage(String specificMessage);

    void displayDBMappingAlreadyExistErrorMessage(String specificMessage);

    void displayEmptyFieldsErrorMessage(String specificMessage);

    void displayRuleParserErrorMessage(String specificMessage);

    void displayDatabaseErrorMessage(String specificMessage);

    void displayQueryParserErrorMessage(String specificMessage);

    void displayUnsupportedAxiomsErrorMessage(String specificMessage);

    void displayPrologEngineCreationErrorMessage(String specificMessage);

    void displayInconsistentOntologyErrorMessage(String specificMessage);

    void displayQueryExecutingErrorMessage(String specificMessage);

    void displayInvalidFileErrorMessage(String specificMessage);

    void displayLockedTableErrorMessage(String specificMessage);

    void displayNoTablesAddedErrorMessage(String specificMessage);

    void displayUnknownErrorMessage(String specificMessage);
    //ADDED----------------------
    /**
     * Display an error message indicating that the submitted action could not be executed because the user does not
     * have permission to execute it.
     */
    void displayPermissionDeniedErrorMessage();

    /**
     * Display an error message indicating that the submitted action could not be executed because the user does not
     * have permission to execute it.
     */
    void displayPermissionDeniedErrorMessage(String specificMessage);

    /**
     * Display an error message for an {@link com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException}
     */
    void displayIncompatibleRemoteServiceExceptionErrorMessage();

    /**
     * Display an error message for an {@link com.google.gwt.user.client.rpc.InvocationException}.
     * @param exception The {@link com.google.gwt.user.client.rpc.InvocationException} that was thrown.
     */
    void displayInvocationExceptionErrorMessage(InvocationException exception);

    /**
     * Display a general error message.
     * @param title The error message title.  Not {@code null}.
     * @param message The error message.  Not {@code null}.
     */
    void displayGeneralErrorMessage(String title, String message);

}
