package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;

import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface AddColumnDialogView extends IsWidget, HasInitialFocusable {

    interface ResetLangTagHandler {
        void handleResetLangTag();
    }

    interface LangTagChangedHandler {
        void handleLangTagChanged();
    }

    String getColumnText();

    public String getTable();

    public Boolean getIsFloating();

    void setRowElements(NohrColumnsTable row, Integer index);

    void setTablesInListBox(List<String> tables);

    void clear();

}
