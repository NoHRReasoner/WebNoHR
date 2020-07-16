package edu.stanford.bmir.protege.web.client.projectsettings;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrDatabaseSettingsView extends IsWidget {

    String getOdbcName();

    String getDatabaseName();

    String getDatabaseType();

    String getUsername();

    String getPassword();

    boolean isValidateInput();

    void setDatabaseSettings(NohrDatabaseSettings nohrDatabaseSettings);

    NohrDatabaseSettings getDatabaseSettings();

    void disableAddButton();

    void enableAddButton();

    void deleteButtonClick(ClickEvent event);

    /*HandlerRegistration deleteClickHandler(ClickHandler handler);*/

    HandlerRegistration addClickHandler(ClickHandler handler);
}
