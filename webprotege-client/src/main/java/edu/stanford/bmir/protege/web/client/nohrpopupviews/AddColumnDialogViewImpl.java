package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.library.text.ExpandingTextBoxImpl;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;

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
public class AddColumnDialogViewImpl extends Composite implements AddColumnDialogView {

    interface AddColumnDialogViewUiBinder extends UiBinder<HTMLPanel, AddColumnDialogViewImpl> {
    }

    private static AddColumnDialogViewUiBinder ourUiBinder = GWT.create(AddColumnDialogViewUiBinder.class);

    @UiField
    ListBox tableListBox;

    @UiField
    ExpandingTextBoxImpl columnTextBox;

    @UiField
    CheckBox isFloating;


    @Nonnull
    private final Messages messages;

    @Inject
    public AddColumnDialogViewImpl(@Nonnull Messages messages) {
        this.messages = checkNotNull(messages);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Nonnull
    @Override
    public String getColumnText() {
        return columnTextBox.getText().trim();
    }

    @Nonnull
    @Override
    public String getTable() {
        return tableListBox.getSelectedItemText();
    }

    @Nonnull
    @Override
    public Boolean getIsFloating() {
        return isFloating.getValue();
    }

    @Override
    public void clear() {
        columnTextBox.setText("");
        tableListBox.clear();
        isFloating.setValue(false);
    }

    @Override
    public void setTablesInListBox(List<String> tables) {
        for (String t : tables)
            tableListBox.addItem(t);
    }

    @Override
    public void setRowElements(NohrColumnsTable row, Integer index) {
        columnTextBox.setText(row.getColumnCol());
        tableListBox.setSelectedIndex(index);
        isFloating.setValue(row.getFloatingCol());
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> columnTextBox.setFocus(true));
    }

    /*@Override
    public void setAcceptKeyHandler(AcceptKeyHandler acceptKey) {
        columnTextBox.setAcceptKeyHandler(acceptKey);
    }*/

    @Override
    protected void onAttach() {
        super.onAttach();
        columnTextBox.setFocus(true);
    }

    /*@UiHandler("columnTextBox")
    public void onChangeEvent(NohrRuleChangeEvent event) {
        *//*Window.alert("Name TextBox has Changed to: " + textBox.getText());*//*
    }*/
}