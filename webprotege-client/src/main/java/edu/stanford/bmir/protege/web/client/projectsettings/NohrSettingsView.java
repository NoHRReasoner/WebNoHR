package edu.stanford.bmir.protege.web.client.projectsettings;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrSettingsView extends IsWidget {

    boolean getELSetting();

    boolean getQLSetting();

    boolean getRLSetting();

    String getEngineSetting();

    void setSettings(NohrSettings nohrSettings);
}
