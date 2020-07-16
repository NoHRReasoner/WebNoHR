package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.gwt.user.client.ui.FormPanel.ENCODING_MULTIPART;
import static com.google.gwt.user.client.ui.FormPanel.METHOD_POST;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UploadRulesDialogViewImpl extends Composite implements UploadRulesDialogView {

    interface UploadRulesDialogViewImplUiBinder extends UiBinder<HTMLPanel, UploadRulesDialogViewImpl> {
    }

    private static UploadRulesDialogViewImplUiBinder ourUiBinder = GWT.create(UploadRulesDialogViewImplUiBinder.class);

    @UiField
    Label rulesLabel;

    @UiField
    FileUpload fileUpload;

    @UiField
    FormPanel formPanel;

    @UiField
    HTMLPanel fileUploadArea;

    @Nonnull
    private final Messages messages;

    private HandlerRegistration submitCompleteHandlerRegistraion = () -> {};

    @Inject
    public UploadRulesDialogViewImpl(@Nonnull Messages messages) {
        this.messages = checkNotNull(messages);
        fileUpload = new FileUpload();
        formPanel = new FormPanel();
        fileUpload.setEnabled(true);

        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setEntityType(@Nonnull EntityType<?> entityType) {
        rulesLabel.setText("Rule Editor");
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> fileUpload.setFocus(true));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        fileUpload.setFocus(true);
    }

    @Override
    public FileUpload getFile() {
        return fileUpload;
    }

    @Override
    public void setFileUploadPostUrl(@Nonnull String url) {
        fileUpload.setName("file");
        formPanel.setMethod(METHOD_POST);
        formPanel.setEncoding(ENCODING_MULTIPART);
        formPanel.setAction(checkNotNull(url));
    }

    @Override
    public void setFileUploadEnabled(boolean enabled) {
        fileUpload.setEnabled(enabled);
        fileUploadArea.setVisible(enabled);
    }

    @Override
    public boolean isFileUploadSpecified() {
        String filename = fileUpload.getFilename();
        return !filename.trim().isEmpty();
    }

    @Override
    public void submitFormData() {
        formPanel.submit();
    }

    @Override
    public void setSubmitCompleteHandler(@Nonnull FormPanel.SubmitCompleteHandler handler) {
        submitCompleteHandlerRegistraion.removeHandler();
        submitCompleteHandlerRegistraion = formPanel.addSubmitCompleteHandler(handler);
    }


    /*@UiHandler("textBox")
    public void onChangeEvent(NohrRuleChangeEvent event) {
        Window.alert("Name TextBox has Changed to: " + textBox.getText());
    }*/
}