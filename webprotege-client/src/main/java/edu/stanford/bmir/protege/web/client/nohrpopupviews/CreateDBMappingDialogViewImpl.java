package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionModel;
import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.csv.CSVGridResources;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.library.dlg.AcceptKeyHandler;
import edu.stanford.bmir.protege.web.client.library.dlg.HasAcceptKeyHandler;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.library.text.ExpandingTextBoxImpl;
import edu.stanford.bmir.protege.web.client.nohrUIElements.ExtendedTextArea;
import edu.stanford.bmir.protege.web.client.nohrUIElements.NohrRuleChangeEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class CreateDBMappingDialogViewImpl extends Composite implements CreateDBMappingDialogView, HasAcceptKeyHandler {

    interface CreateDBMappingDialogViewImplUiBinder extends UiBinder<HTMLPanel, CreateDBMappingDialogViewImpl> {
    }

    private static CreateDBMappingDialogViewImplUiBinder ourUiBinder = GWT.create(CreateDBMappingDialogViewImplUiBinder.class);

    @Nonnull
    private final AddColumnPresenter addColumnPresenter;

    @Nonnull
    private final AddTablePresenter addTablePresenter;

    @UiField(provided = true)
    protected DataGrid<NohrColumnsTable> columnsGrid;

    private final ListDataProvider<NohrColumnsTable> columnsGridProvider;

    @UiField(provided = true)
    protected DataGrid<NohrTablesTable> tablesGrid;

    private final ListDataProvider<NohrTablesTable> tablesGridProvider;

    @UiField
    ListBox odbcListBox;

    @UiField
    ExpandingTextBoxImpl predicateTextBox;

    @UiField
    TextBox arityTextBox;

    @UiField
    ExtendedTextArea sqlTextBox;

    @UiField
    Button addRowsToColumnsTableButton;

    @UiField
    Button deleteRowsInColumnsTableButton;

    @UiField
    Button addRowsToTablesTableButton;

    @UiField
    Button deleteRowsInTablesTableButton;

    @UiField
    Button writeSQLButton;

    final SelectionModel<NohrColumnsTable> columnsTableSelectionModel = new MultiSelectionModel<NohrColumnsTable>();

    final SelectionModel<NohrTablesTable> tablesTableSelectionModel = new MultiSelectionModel<NohrTablesTable>();

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Nonnull
    private final Messages messages;

    @Inject
    public CreateDBMappingDialogViewImpl(@Nonnull Messages messages,
                                         @Nonnull AddColumnPresenter addColumnPresenter,
                                         @Nonnull AddTablePresenter addTablePresenter,
                                         DispatchErrorMessageDisplay errorMessageDisplay) {
        this.addColumnPresenter = addColumnPresenter;
        this.addTablePresenter = addTablePresenter;
        this.errorMessageDisplay = errorMessageDisplay;

        columnsGrid = new DataGrid<NohrColumnsTable>(Integer.MAX_VALUE, CSVGridResources.INSTANCE);

        columnsGrid.setSelectionModel(columnsTableSelectionModel);
        addColumnsTableSelectionHandler(this::handleSelectionRowColumnsChangedInView);
        addColumnsGridDoubleClickHandler(this::handleDoubleClickOnColumnsTable);

        columnsGrid.addColumn(new TablesColumn(), "Table");
        columnsGrid.addColumn(new ColumnsColumn(), "Column");

        columnsGrid.getColumn(0).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        columnsGrid.getColumn(1).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

        columnsGrid.setColumnWidth(0, 70, Style.Unit.PX);
        columnsGrid.setColumnWidth(1, 70, Style.Unit.PX);

        columnsGridProvider = new ListDataProvider<NohrColumnsTable>(new ArrayList<NohrColumnsTable>());
        columnsGridProvider.addDataDisplay(columnsGrid);

        tablesGrid = new DataGrid<NohrTablesTable>(Integer.MAX_VALUE, CSVGridResources.INSTANCE);

        tablesGrid.setSelectionModel(tablesTableSelectionModel);
        addTablesTableSelectionHandler(this::handleSelectionRowTablesChangedInView);
        addTablesGridDoubleClickHandler(this::handleDoubleClickOnTablesTable);

        tablesGrid.addColumn(new TablesColumnFromTablesGrid(), "Table");
        tablesGrid.addColumn(new ColumnsColumnFromTablesGrid(), "Column");
        tablesGrid.addColumn(new JoinTableColumnFromTablesGrid(), "JOIN Table");
        tablesGrid.addColumn(new OnColumnsColumnFromTablesGrid(), "ON Column");

        tablesGrid.getColumn(0).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        tablesGrid.getColumn(1).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        tablesGrid.getColumn(2).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        tablesGrid.getColumn(3).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

        tablesGrid.setColumnWidth(0, 70, Style.Unit.PX);
        tablesGrid.setColumnWidth(1, 70, Style.Unit.PX);
        tablesGrid.setColumnWidth(2, 70, Style.Unit.PX);
        tablesGrid.setColumnWidth(3, 70, Style.Unit.PX);

        tablesGridProvider = new ListDataProvider<NohrTablesTable>(new ArrayList<NohrTablesTable>());
        tablesGridProvider.addDataDisplay(tablesGrid);

        this.messages = checkNotNull(messages);
        initWidget(ourUiBinder.createAndBindUi(this));

    }

    private void handleSelectionRowTablesChangedInView(SelectionChangeEvent selectionChangeEvent) {
        getSelectedRowOnTablesTable();
    }

    private void handleSelectionRowColumnsChangedInView(SelectionChangeEvent selectionChangeEvent) {
        getSelectedRowOnColumnsTable();
    }

    private void handleDoubleClickOnColumnsTable(DoubleClickEvent doubleClickEvent) {
        NohrColumnsTable selectedItem = getSelectedRowOnColumnsTable();

        List<NohrTablesTable> tablesGridList = tablesGridProvider.getList();

        List<String> tables = new LinkedList<>();
        for (int i = 0; i < tablesGridList.size(); i++) {
            tables.add(tablesGridList.get(i).getTableWithNumber());
        }

        Integer elementIndex = -1;
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).equals(selectedItem.getTableWithNumber())) {
                elementIndex = i;
                break;
            }
        }
        addColumnPresenter.updateRows(this::handleNoHRRowToColumnUpdated, selectedItem, tables, elementIndex);
    }

    private void handleNoHRRowToColumnUpdated(NohrColumnsTable oldElement, NohrColumnsTable newElement) {
        List<NohrColumnsTable> contents = columnsGridProvider.getList();
        for (int i = 0; i < contents.size(); i++) {
            NohrColumnsTable elem = contents.get(i);
            if (elem.equals(oldElement)) {
                contents.set(i, newElement);
            }
        }
        columnsGridProvider.setList(contents);
    }

    private void handleDoubleClickOnTablesTable(DoubleClickEvent doubleClickEvent) {
        NohrTablesTable selectedItem = getSelectedRowOnTablesTable();

        Boolean isFirstElement = false;
        addTablePresenter.enableElements();
        if (selectedItem.getJoinNumber() == -1) {
            isFirstElement = true;
            addTablePresenter.disableElements();
        }

        Integer elementIndex = -1;
        List<NohrTablesTable> tablesGridList = tablesGridProvider.getList();
        List<String> tables = new LinkedList<>();
        for (int i = 0; i < tablesGridList.size(); i++) {
            if (!tablesGridList.get(i).equals(selectedItem)) {
                tables.add(tablesGridList.get(i).getTableWithNumber());
            }
        }

        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).equals(selectedItem.getJoinTableWithNumber())) {
                elementIndex = i;
                break;
            }
        }

        addTablePresenter.updateRows(this::handleNoHRRowToTableUpdated, selectedItem, tables, isFirstElement, elementIndex);
    }

    private void handleNoHRRowToTableUpdated(NohrTablesTable oldElement, NohrTablesTable newElement) {
        List<NohrTablesTable> contents = tablesGridProvider.getList();
        for (int i = 0; i < contents.size(); i++) {
            NohrTablesTable elem = contents.get(i);
            if (elem.equals(oldElement)) {
                contents.set(i, newElement);
            }
            if (elem.getJoinTable().equals(oldElement.getTable())) {
                elem.setJoinTable(newElement.getTable());
            }
        }
        tablesGridProvider.setList(contents);
    }


    @UiHandler("addRowsToColumnsTableButton")
    public void addRowsToColumnsTableButtonHandler(ClickEvent event) {

        List<NohrTablesTable> tablesGridList = tablesGridProvider.getList();
        if (tablesGridList.isEmpty()) {
            errorMessageDisplay.displayNoTablesAddedErrorMessage("There is no tables added to select a column");
            return;
        }

        List<String> tables = new LinkedList<>();
        for (int i = 0; i < tablesGridList.size(); i++) {
            tables.add(tablesGridList.get(i).getTableWithNumber());
        }

        addColumnPresenter.createRows(this::handleNoHRRowToColumnAdded, tables);
    }

    private void handleNoHRRowToColumnAdded(NohrColumnsTable element) {
        Integer incArity = getArityNumber() + 1;
        arityTextBox.setValue(incArity.toString());
        List<NohrColumnsTable> contents = columnsGridProvider.getList();
        contents.add(element);
        columnsGridProvider.setList(contents);
    }

    @UiHandler("addRowsToTablesTableButton")
    public void addRowsToTablesTableButtonHandler(ClickEvent event) {
        List<NohrTablesTable> tablesGridList = tablesGridProvider.getList();

        if (tablesGridList.isEmpty())
            addTablePresenter.disableElements();
        else
            addTablePresenter.enableElements();

        Boolean isFirstElement = true;
        List<String> tables = new LinkedList<>();
        for (int i = 0; i < tablesGridList.size(); i++) {
            tables.add(tablesGridList.get(i).getTableWithNumber());
            isFirstElement = false;
        }
        addTablePresenter.createRows(this::handleNoHRRowToTableAdded, tables, isFirstElement);
    }

    private void handleNoHRRowToTableAdded(NohrTablesTable element) {

        if (!element.getJoinTable().isEmpty()) {
            String joinTableName = element.getJoinTable().split(" as t")[0];
            element.setJoinTable(joinTableName);
        }
        List<NohrTablesTable> contents = tablesGridProvider.getList();
        if (!contents.isEmpty()) {
            NohrTablesTable lastElement = contents.get(contents.size() - 1);
            element.setTableNumber(lastElement.getNumber() + 1);
        } else {
            element.setTableNumber(1);
        }
        contents.add(element);
        tablesGridProvider.setList(contents);
    }

    @UiHandler("deleteRowsInColumnsTableButton")
    public void deleteRowsToColumnsTableButtonHandler(ClickEvent event) {
        Integer decArity = getArityNumber();
        List<NohrColumnsTable> contents = columnsGridProvider.getList();
        for (NohrColumnsTable item : contents) {
            if (item.equals(getSelectedRowOnColumnsTable())) {
                contents.remove(item);
                decArity--;
            }
        }
        arityTextBox.setValue(decArity.toString());
        columnsGridProvider.setList(contents);
        deleteRowsInColumnsTableButton.setText("delete");
    }

    @UiHandler("deleteRowsInTablesTableButton")
    public void deleteRowsToTablesTableButtonHandler(ClickEvent event) {
        NohrTablesTable selectedItem = getSelectedRowOnTablesTable();

        List<NohrTablesTable> contents = tablesGridProvider.getList();

        for (NohrTablesTable elem : contents) {
            if (elem.getJoinTable().equals(selectedItem.getTable())) {
                errorMessageDisplay.displayLockedTableErrorMessage("The table is currently locked");
                return;
            }
        }

        for (NohrTablesTable item : contents) {
            if (item.equals(selectedItem))
                contents.remove(item);
        }
        tablesGridProvider.setList(contents);
        deleteRowsInTablesTableButton.setText("delete");
    }

    @UiHandler("writeSQLButton")
    public void EnableSQLWritingButtonHandler(ClickEvent event) {
        enableSQLWriting();
    }

    private static final TextCell CELL = new TextCell();

    private class TablesColumn extends Column<NohrColumnsTable, String> {

        private TablesColumn() {
            super(CELL);
        }

        @Override
        public String getValue(NohrColumnsTable object) {
            return object.getTableNumber();
        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }


    private static class ColumnsColumn extends Column<NohrColumnsTable, String> {

        private ColumnsColumn() {
            super(CELL);
        }

        @Override
        public String getValue(NohrColumnsTable object) {
            return object.getColumnCol();

        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }

    private class TablesColumnFromTablesGrid extends Column<NohrTablesTable, String> {


        private TablesColumnFromTablesGrid() {
            super(CELL);
        }

        @Override
        public String getValue(NohrTablesTable object) {
            return object.getTableWithNumber();
        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }


    private static class ColumnsColumnFromTablesGrid extends Column<NohrTablesTable, String> {

        private ColumnsColumnFromTablesGrid() {
            super(CELL);
        }

        @Override
        public String getValue(NohrTablesTable object) {
            return object.getColumn();

        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }

    private static class JoinTableColumnFromTablesGrid extends Column<NohrTablesTable, String> {

        private JoinTableColumnFromTablesGrid() {
            super(CELL);
        }

        @Override
        public String getValue(NohrTablesTable object) {
            return object.getTableJoinNumber();

        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }

    private static class OnColumnsColumnFromTablesGrid extends Column<NohrTablesTable, String> {

        private OnColumnsColumnFromTablesGrid() {
            super(CELL);
        }

        @Override
        public String getValue(NohrTablesTable object) {
            return object.getOnColumn();

        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }

    @Override
    public void setDatabaseSettings(List<NohrDatabaseSettings> databaseSettings) {
        odbcListBox.clear();
        arityTextBox.setText("0");
        for (NohrDatabaseSettings settings : databaseSettings) {
            odbcListBox.addItem(settings.getConnectionName());
        }
        odbcListBox.setSelectedIndex(0);
    }

    @Override
    public void setEntityType(@Nonnull EntityType<?> entityType) {
        /*odbcLabel.setText("Database Mapping Editor");*/
    }

    @Nonnull
    @Override
    public boolean isValidInput() {
        if (writeSQLButton.isEnabled())
            return !getODBC().isEmpty() && !getPredicateText().isEmpty() && !columnsGridProvider.getList().isEmpty() && !tablesGridProvider.getList().isEmpty();
        else
            return !getODBC().isEmpty() && !getPredicateText().isEmpty() && !getSQLText().isEmpty();
    }

    @Nonnull
    @Override
    public String getDBMappingInfo() {
        return predicateTextBox.getText().trim() + " <- " + sqlTextBox.getText().trim();
    }

    @Nonnull
    @Override
    public String getODBC() {
        if(odbcListBox.getSelectedItemText() == null)
            return "";
        return odbcListBox.getSelectedItemText();
    }

    @Nonnull
    @Override
    public String getPredicateText() {
        return predicateTextBox.getText().trim();
    }

    @Nonnull
    @Override
    public int getArityNumber() {
        String arityStr = arityTextBox.getText().trim();
        int arity = 0;

        if (arityStr.isEmpty())
            return 0;
        try {
            arity = Integer.parseInt(arityStr);
        } catch (NumberFormatException ex) {
            return 0;
        }
        return arity;
    }

    @Nonnull
    @Override
    public String getSQLText() {
        return sqlTextBox.getText().trim();
    }

    @Override
    public void clear() {
        odbcListBox.clear();
        tablesGridProvider.setList(new LinkedList<>());
        columnsGridProvider.setList(new LinkedList<>());
        predicateTextBox.setText("");
        arityTextBox.setText("");
        sqlTextBox.setText("");

        disableSQLWriting();
    }

    @Override
    public void setInfo(String odbc, List<NohrColumnsTable> cols, List<NohrTablesTable> tables, String predicate, Integer arity, String sql) {
        for (int i = 0; i< odbcListBox.getItemCount();i++) {
            if(odbcListBox.getItemText(i).equals(odbc)) {
                odbcListBox.setSelectedIndex(i);
                break;
            }
        }
        columnsGridProvider.setList(cols);
        tablesGridProvider.setList(tables);

        predicateTextBox.setText(predicate);
        arityTextBox.setText(arity.toString());
        sqlTextBox.setText(sql);
    }

    @Override
    public boolean autoSQLWriting() {
        return writeSQLButton.isEnabled();
    }

    @Override
    public List<NohrColumnsTable> getSelectColumnsList() {
        return columnsGridProvider.getList();
    }

    @Override
    public List<NohrTablesTable> getFromTablesList() {
        return tablesGridProvider.getList();
    }

    private void disableSQLWriting() {
        deleteRowsInColumnsTableButton.setEnabled(true);
        deleteRowsInTablesTableButton.setEnabled(true);
        addRowsToColumnsTableButton.setEnabled(true);
        addRowsToTablesTableButton.setEnabled(true);

        arityTextBox.setEnabled(false);
        sqlTextBox.setEnabled(false);
        writeSQLButton.setEnabled(true);
    }

    private void enableSQLWriting() {
        tablesGridProvider.setList(new LinkedList<>());
        columnsGridProvider.setList(new LinkedList<>());
        deleteRowsInColumnsTableButton.setEnabled(false);
        deleteRowsInTablesTableButton.setEnabled(false);
        addRowsToColumnsTableButton.setEnabled(false);
        addRowsToTablesTableButton.setEnabled(false);

        arityTextBox.setEnabled(true);
        sqlTextBox.setEnabled(true);
        writeSQLButton.setEnabled(false);
    }

    @Override
    public Optional<HasRequestFocus> getInitialFocusable() {
        return Optional.of(() -> odbcListBox.setFocus(true));
    }

    @Override
    public void setAcceptKeyHandler(AcceptKeyHandler acceptKey) {
        predicateTextBox.setAcceptKeyHandler(acceptKey);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        predicateTextBox.setFocus(true);
    }

    @Override
    public HandlerRegistration addTablesTableSelectionHandler(SelectionChangeEvent.Handler handler) {
        return tablesTableSelectionModel.addSelectionChangeHandler(handler);
    }

    @Override
    public HandlerRegistration addColumnsTableSelectionHandler(SelectionChangeEvent.Handler handler) {
        return columnsTableSelectionModel.addSelectionChangeHandler(handler);
    }

    @Override
    public NohrColumnsTable getSelectedRowOnColumnsTable() {
        NohrColumnsTable selectedItem = ((MultiSelectionModel<NohrColumnsTable>) columnsTableSelectionModel).getSelectedSet().iterator().next();
        deleteRowsInColumnsTableButton.setText("delete " + selectedItem.getColumnCol() + " column");
        return selectedItem;
    }

    @Override
    public NohrTablesTable getSelectedRowOnTablesTable() {
        NohrTablesTable selectedItem = ((MultiSelectionModel<NohrTablesTable>) tablesTableSelectionModel).getSelectedSet().iterator().next();
        deleteRowsInTablesTableButton.setText("delete " + selectedItem.getTableWithNumber() + " table");
        return selectedItem;
    }

    @Override
    public HandlerRegistration addTablesGridDoubleClickHandler(DoubleClickHandler handler) {
        return tablesGrid.addDomHandler(handler, DoubleClickEvent.getType());
    }

    @Override
    public HandlerRegistration addColumnsGridDoubleClickHandler(DoubleClickHandler handler) {
        return columnsGrid.addDomHandler(handler, DoubleClickEvent.getType());
    }

    @UiHandler("sqlTextBox")
    public void onChangeEvent(NohrRuleChangeEvent event) {
        /*Window.alert("Name TextBox has Changed to: " + textBox.getText());*/
    }
}