package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface UploadDBMappingDialogView extends IsWidget, HasInitialFocusable {

    void setEntityType(@Nonnull EntityType<?> entityType);

    FileUpload getFile();

    void setFileUploadPostUrl(@Nonnull String url);

    boolean isFileUploadSpecified();

    void submitFormData();

    void setFileUploadEnabled(boolean enabled);

    void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler);

    /*File getFile();*/

}
