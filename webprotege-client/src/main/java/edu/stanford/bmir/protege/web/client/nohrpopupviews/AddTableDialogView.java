package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface AddTableDialogView extends IsWidget, HasInitialFocusable {

    interface ResetLangTagHandler {
        void handleResetLangTag();
    }

    interface LangTagChangedHandler {
        void handleLangTagChanged();
    }

    @Nonnull
    String getTable();

    String getColumn();

    String getJoinTable();

    String getOnColumn();

    void setRowElements(NohrTablesTable row, Integer index);

    void disableElements();

    void enableElements();

    void setTablesInListBox(List<String> tables);

    void setJoinTableItem(Integer index);

    void clear();

}
