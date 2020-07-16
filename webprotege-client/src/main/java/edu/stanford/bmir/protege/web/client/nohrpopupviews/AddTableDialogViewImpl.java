package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;

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
public class AddTableDialogViewImpl extends Composite implements AddTableDialogView {

    interface AddTableDialogViewImplUiBinder extends UiBinder<HTMLPanel, AddTableDialogViewImpl> {
    }

    private static AddTableDialogViewImplUiBinder ourUiBinder = GWT.create(AddTableDialogViewImplUiBinder.class);

    @UiField
    TextBox tableTextBox;

    @UiField
    TextBox columnTextBox;

    @UiField
    ListBox joinTableTextBox;

    @UiField
    TextBox onColumnTextBox;

    @Nonnull
    private final Messages messages;

    @Inject
    public AddTableDialogViewImpl(@Nonnull Messages messages) {
        /*columnTextBox.setEnabled(true);
        joinTableTextBox.setEnabled(true);
        onColumnTextBox.setEnabled(true);*/
        this.messages = checkNotNull(messages);
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Nonnull
    @Override
    public String getTable() {
        return tableTextBox.getText().trim();
    }

    @Nonnull
    @Override
    public String getColumn() {
        return columnTextBox.getText().trim();
    }

    @Nonnull
    @Override
    public String getJoinTable() {
        if(joinTableTextBox.getSelectedItemText() == null)
            return "";
        return joinTableTextBox.getSelectedItemText();
    }

    @Nonnull
    @Override
    public String getOnColumn() {
        return onColumnTextBox.getText().trim();
    }

    public void setRowElements(NohrTablesTable row, Integer index) {
        tableTextBox.setValue(row.getTable());
        columnTextBox.setValue(row.getColumn());
        joinTableTextBox.setSelectedIndex(index);
        onColumnTextBox.setValue(row.getOnColumn());
    }

    private int getElementIndex(String table) {
        for (int i = 0;i<joinTableTextBox.getItemCount();i++) {
            if(joinTableTextBox.getItemText(i).equals(table))
                return i;
        }
        return -1;
    }

    @Override
    public void disableElements() {
        columnTextBox.setEnabled(false);
        joinTableTextBox.setEnabled(false);
        onColumnTextBox.setEnabled(false);
    }

    @Override
    public void enableElements() {
        columnTextBox.setEnabled(true);
        joinTableTextBox.setEnabled(true);
        onColumnTextBox.setEnabled(true);
    }

    @Override
    public void setTablesInListBox(List<String> tables) {
        for (String t : tables)
            joinTableTextBox.addItem(t);
    }

    @Override
    public void setJoinTableItem(Integer index) {
        joinTableTextBox.setSelectedIndex(index);
    }

    @Override
    public void clear() {
        tableTextBox.setText("");
        columnTextBox.setText("");
        joinTableTextBox.clear();
        onColumnTextBox.setText("");
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> tableTextBox.setFocus(true));
    }

    /*@Override
    public void setAcceptKeyHandler(AcceptKeyHandler acceptKey) {
        tableTextBox.setAcceptKeyHandler(acceptKey);
    }*/

    @Override
    protected void onAttach() {
        super.onAttach();
        tableTextBox.setFocus(true);
    }

    /*@UiHandler("tableTextBox")
    public void onChangeEvent(NohrRuleChangeEvent event) {

    }*/
}