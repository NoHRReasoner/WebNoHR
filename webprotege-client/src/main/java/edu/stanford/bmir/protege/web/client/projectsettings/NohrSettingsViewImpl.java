package edu.stanford.bmir.protege.web.client.projectsettings;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;

import javax.inject.Inject;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
@AutoValue
@GwtCompatible(serializable = true)
public class NohrSettingsViewImpl extends Composite implements NohrSettingsView {

    interface NohrSettingsViewImplUiBinder extends UiBinder<HTMLPanel, NohrSettingsViewImpl> {

    }

    private static NohrSettingsViewImplUiBinder ourUiBinder = GWT.create(NohrSettingsViewImplUiBinder.class);

    @UiField
    ListBox dlEngineSelectorListBox;

    @UiField
    CheckBox elCheckBox;

    @UiField
    CheckBox qlCheckBox;

    @UiField
    CheckBox rlCheckBox;

    @Inject
    public NohrSettingsViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dlEngineSelectorListBox.addItem("HERMIT");
        dlEngineSelectorListBox.addItem("KONCLUDE");
        dlEngineSelectorListBox.setSelectedIndex(0);
    }

    @Override
    public void setSettings(NohrSettings nohrSettings) {

        elCheckBox.setValue(nohrSettings.getELSetting());
        qlCheckBox.setValue(nohrSettings.getQLSetting());
        rlCheckBox.setValue(nohrSettings.getRLSetting());
        if (nohrSettings.getDLEngineSetting().equals("HERMIT"))
            dlEngineSelectorListBox.setSelectedIndex(0);
        else
            dlEngineSelectorListBox.setSelectedIndex(1);
    }

    @Override
    public boolean getELSetting() {
        return elCheckBox.getValue();
    }

    @Override
    public boolean getQLSetting() {
        return qlCheckBox.getValue();
    }

    @Override
    public boolean getRLSetting() {
        return rlCheckBox.getValue();
    }

    @Override
    public String getEngineSetting() {
        return dlEngineSelectorListBox.getSelectedValue();
    }
}