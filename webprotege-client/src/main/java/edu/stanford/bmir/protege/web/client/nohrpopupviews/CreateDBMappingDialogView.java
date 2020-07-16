package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.view.client.SelectionChangeEvent;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface CreateDBMappingDialogView extends IsWidget, HasInitialFocusable {

    void setDatabaseSettings(List<NohrDatabaseSettings> databaseSettings);

    interface ResetLangTagHandler {
        void handleResetLangTag();
    }

    interface LangTagChangedHandler {
        void handleLangTagChanged();
    }

    void setEntityType(@Nonnull EntityType<?> entityType);

    @Nonnull
    String getODBC();

    @Nonnull
    String getPredicateText();

    @Nonnull
    int getArityNumber();

    @Nonnull
    String getSQLText();

    @Nonnull
    String getDBMappingInfo();

    @Nonnull
    boolean isValidInput();

    void clear();

    void setInfo(String odbc, List<NohrColumnsTable> cols, List<NohrTablesTable> tables, String predicate, Integer arity, String sql);

    boolean autoSQLWriting();

    List<NohrColumnsTable> getSelectColumnsList();

    List<NohrTablesTable> getFromTablesList();

    /*void disableSQLWriting();

    void enableSQLWriting();*/

    HandlerRegistration addTablesTableSelectionHandler(SelectionChangeEvent.Handler handler);

    HandlerRegistration addColumnsGridDoubleClickHandler(DoubleClickHandler handler);

    HandlerRegistration addTablesGridDoubleClickHandler(DoubleClickHandler handler);

    HandlerRegistration addColumnsTableSelectionHandler(SelectionChangeEvent.Handler handler);

    NohrColumnsTable getSelectedRowOnColumnsTable();

    NohrTablesTable getSelectedRowOnTablesTable();

}
