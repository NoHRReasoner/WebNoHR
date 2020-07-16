package edu.stanford.bmir.protege.web.client.projectsettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettingsImpl;

import javax.inject.Inject;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
/*@AutoValue
@GwtCompatible(serializable = true)*/
public class NohrDatabaseSettingsViewImpl extends Composite implements NohrDatabaseSettingsView {

    interface NohrDatabaseSettingsViewImplUiBinder extends UiBinder<HTMLPanel, NohrDatabaseSettingsViewImpl> {

    }

    private static NohrDatabaseSettingsViewImplUiBinder ourUiBinder = GWT.create(NohrDatabaseSettingsViewImplUiBinder.class);

    /*@UiField(provided = true)
    ValueListFlexEditorImpl<DictionaryLanguageData> databaseSettingsListFlexEditor;*/

    /*@UiField
    edu.stanford.bmir.protege.web.client.list.ListBox<String, NohrRule> dbMappings;*/

    @UiField
    TextBox odbcTextBox;

    @UiField
    TextBox databaseNameTextBox;

    @UiField
    ListBox databaseTypeListBox;

    @UiField
    TextBox databaseUsernameTextBox;

    @UiField
    PasswordTextBox databasePasswordTextBox;

    @UiField
    Button deleteButton;

    @UiField
    Button addButton;

    /*@UiField
    SimplePanel criteriaContainer;*/

    @Inject
    public NohrDatabaseSettingsViewImpl(/*@Nonnull Provider<NohrDatabaseSettingsDataEditor> editorProvider*/) {
        /*databaseSettingsListFlexEditor = new ValueListFlexEditorImpl<>(editorProvider::get);
        databaseSettingsListFlexEditor.setEnabled(true);
        databaseSettingsListFlexEditor.setNewRowMode(ValueListEditor.NewRowMode.MANUAL);*/
        /*buttonClickHandler(this::deleteButtonClick);*/
        initWidget(ourUiBinder.createAndBindUi(this));
        databaseTypeListBox.addItem("MYSQL");
        databaseTypeListBox.addItem("ORACLE");
        databaseTypeListBox.setSelectedIndex(0);
    }

    @Override
    public void setDatabaseSettings(NohrDatabaseSettings nohrDatabaseSettings) {
        odbcTextBox.setValue(nohrDatabaseSettings.getConnectionName());
        databaseNameTextBox.setValue(nohrDatabaseSettings.getDatabaseName());
        if (nohrDatabaseSettings.getDatabaseType().equals("MySQL"))
            databaseTypeListBox.setSelectedIndex(0);
        else
            databaseTypeListBox.setSelectedIndex(1);
        databaseUsernameTextBox.setValue(nohrDatabaseSettings.getUsername());
        databasePasswordTextBox.setValue(nohrDatabaseSettings.getPassword());
    }

    @Override
    public NohrDatabaseSettings getDatabaseSettings() {
        NohrDatabaseSettings databaseSettings = new NohrDatabaseSettingsImpl();
        databaseSettings.setConnectionName(odbcTextBox.getValue());
        databaseSettings.setDatabaseName(databaseNameTextBox.getValue());
        databaseSettings.setDatabaseType(databaseTypeListBox.getSelectedValue());
        databaseSettings.setUsername(databaseUsernameTextBox.getValue());
        databaseSettings.setPassword(databasePasswordTextBox.getValue());
        return databaseSettings;
    }

    @Override
    public boolean isValidateInput() {
        if (getOdbcName().isEmpty())
            return false;
        if (getDatabaseName().isEmpty())
            return false;
        if (getDatabaseType().isEmpty())
            return false;
        if (getUsername().isEmpty())
            return false;
        if (getPassword().isEmpty())
            return false;

        return true;
    }

    @Override
    public String getOdbcName() {
        return odbcTextBox.getValue();
    }

    @Override
    public String getDatabaseName() {
        return databaseNameTextBox.getValue();
    }

    @Override
    public String getDatabaseType() {
        return databaseTypeListBox.getSelectedValue();
    }

    @Override
    public String getUsername() {
        return databaseUsernameTextBox.getValue();
    }

    @Override
    public String getPassword() {
        return databasePasswordTextBox.getValue();
    }

    @UiHandler("deleteButton")
    public void deleteButtonClick(ClickEvent event) {
        odbcTextBox.setValue("");
        databaseNameTextBox.setValue("");
        databaseTypeListBox.clear();
        databaseUsernameTextBox.setValue("");
        databasePasswordTextBox.setValue("");
        odbcTextBox.setEnabled(false);
        databaseNameTextBox.setEnabled(false);
        databaseTypeListBox.setEnabled(false);
        databaseUsernameTextBox.setEnabled(false);
        databasePasswordTextBox.setEnabled(false);
    }

    @Override
    public void disableAddButton() {
        addButton.setEnabled(false);
        addButton.setVisible(false);
    }

    @Override
    public void enableAddButton() {
        addButton.setEnabled(true);
        addButton.setVisible(true);
    }

   /* @Override
    public HandlerRegistration deleteClickHandler(ClickHandler handler) {
        return deleteButton.addClickHandler(handler);
    }*/

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addButton.addClickHandler(handler);
    }
}