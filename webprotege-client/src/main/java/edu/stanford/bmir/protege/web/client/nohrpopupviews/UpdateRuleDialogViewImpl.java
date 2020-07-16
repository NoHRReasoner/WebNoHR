package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.AcceptKeyHandler;
import edu.stanford.bmir.protege.web.client.library.dlg.HasAcceptKeyHandler;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.library.text.ExpandingTextBoxImpl;
import edu.stanford.bmir.protege.web.client.nohrUIElements.NohrRuleChangeEvent;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UpdateRuleDialogViewImpl extends Composite implements UpdateRuleDialogView, HasAcceptKeyHandler {

    interface UpdateRulesDialogViewImplUiBinder extends UiBinder<HTMLPanel, UpdateRuleDialogViewImpl> {
    }

    private static UpdateRulesDialogViewImplUiBinder ourUiBinder = GWT.create(UpdateRulesDialogViewImplUiBinder.class);

    @UiField
    ExpandingTextBoxImpl textBox;

    @UiField
    Label rulesLabel;

    @Nonnull
    private final Messages messages;

    @Inject
    public UpdateRuleDialogViewImpl(@Nonnull Messages messages) {
        this.messages = checkNotNull(messages);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setEntityType(@Nonnull EntityType<?> entityType) {
        rulesLabel.setText("Rule Editor");
    }

    @Nonnull
    @Override
    public String getText() {
        return textBox.getText().trim();
    }

    @Override
    public void setText(String text) {
        textBox.setText(text);
    }

    @Override
    public void clear() {
        textBox.setText("");
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> textBox.setFocus(true));
    }

    @Override
    public void setAcceptKeyHandler(AcceptKeyHandler acceptKey) {
        textBox.setAcceptKeyHandler(acceptKey);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        textBox.setFocus(true);
    }

    @UiHandler("textBox")
    public void onChangeEvent(NohrRuleChangeEvent event) {
        /*Window.alert("Name TextBox has Changed to: " + textBox.getText());*/
    }
}