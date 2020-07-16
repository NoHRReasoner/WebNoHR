package edu.stanford.bmir.protege.web.client.nohrquery;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import edu.stanford.bmir.protege.web.client.csv.CSVGridResources;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.nohrUIElements.ExtendedTextArea;
import edu.stanford.bmir.protege.web.client.nohrUIElements.NohrRuleChangeEvent;
import edu.stanford.bmir.protege.web.client.progress.BusyView;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.ExecNohrQueryAction;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrquery.AnswersTable;
import edu.stanford.bmir.protege.web.shared.nohrquery.NohrUIAnswer;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NoHRQueryViewImpl extends Composite implements NoHRQueryView {

    interface NoHRQueryViewImplUiBinder extends UiBinder<HTMLPanel, NoHRQueryViewImpl> {

    }

    private static NoHRQueryViewImplUiBinder ourUiBinder = GWT.create(NoHRQueryViewImplUiBinder.class);

    @UiField(provided = true)
    protected DataGrid<AnswersTable> answersGrid;

    private final ListDataProvider<AnswersTable> answersGridProvider;

    @UiField
    protected ExtendedTextArea queryEditor;

    @UiField
    protected BusyView busyView;

    @UiField
    Button execQueryButton;

    @UiField
    CheckBox showTrueAnswersCheckBox;

    @UiField
    CheckBox showUndefinedAnswersCheckBox;

    @UiField
    CheckBox showInconsistentAnswersCheckBox;

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Nonnull
    private final ProjectId projectId;

    private final DispatchServiceManager dsm;

    //private Map<String, List<String>> answersMap;

    private final NohrQueriesProvider nohrQueriesProvider;

    private List<Column> columnsList;

    private int lastColumnsNum;

    @Inject
    public NoHRQueryViewImpl(@Nonnull ProjectId projectId,
                             DispatchServiceManager dispatchServiceManager,
                             DispatchErrorMessageDisplay errorMessageDisplay,
                             NohrQueriesProvider nohrQueriesProvider) {
        this.projectId = projectId;
        this.dsm = dispatchServiceManager;
        this.errorMessageDisplay = errorMessageDisplay;
        //this.answersMap = new HashMap<>();
        this.columnsList = new LinkedList<>();
        this.lastColumnsNum = 2;

        answersGrid = new DataGrid<AnswersTable>(Integer.MAX_VALUE, CSVGridResources.INSTANCE);

        Column trustVal = new TrustValCol();
        answersGrid.addColumn(trustVal, "Truth Values");
        columnsList.add(trustVal);
        Column varCol = new VariableCol(0);
        answersGrid.addColumn(varCol, "Variables");
        columnsList.add(varCol);

        answersGrid.getColumn(0).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        answersGrid.getColumn(1).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

        answersGrid.setColumnWidth(0, 50, Style.Unit.PCT);
        answersGrid.setColumnWidth(1, 50, Style.Unit.PCT);

        answersGridProvider = new ListDataProvider<AnswersTable>(new ArrayList<AnswersTable>());
        answersGridProvider.addDataDisplay(answersGrid);


        initWidget(ourUiBinder.createAndBindUi(this));
        this.execQueryButton.setEnabled(true);
        showTrueAnswersCheckBox.setValue(true);
        showInconsistentAnswersCheckBox.setValue(true);
        showUndefinedAnswersCheckBox.setValue(true);
        this.nohrQueriesProvider = nohrQueriesProvider;

        /*queryEditor.addTextChangeEventHandler(event -> Window.alert("NoHRQueryViewImpl change"));*/
    }

    private static final TextCell CELL = new TextCell();

    private class TrustValCol extends Column<AnswersTable, String> {

        private TrustValCol() {
            super(CELL);
        }

        @Override
        public String getValue(AnswersTable object) {
            return object.getTruthValueCol();
        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }


    private static class VariableCol extends Column<AnswersTable, String> {

        Integer index;

        private VariableCol(Integer index) {
            super(CELL);
            this.index = index;
        }

        @Override
        public String getValue(AnswersTable object) {
            return object.getValues().get(index);

        }

        @Override
        public boolean isSortable() {
            return true;
        }
    }


    @Override
    public void setBusy(boolean busy) {
        GWT.log("Set Busy: " + busy);
        busyView.setVisible(busy);
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<EntityNode>> handler) {
        return null;
    }

    @UiHandler("execQueryButton")
    public void execButtonClick(ClickEvent event) {
        if (!queryEditor.getText().isEmpty()) {
            resetGrid();
            setBusy(true);
            busyView.setMessage("Executing Query");
            for (NoHRQueryPresenter view : nohrQueriesProvider.getViews()) {
                view.getView().getExecQueryButton().setEnabled(false);
                view.getView().getTrueCheckBox().setEnabled(false);
                view.getView().getInconsistentCheckBox().setEnabled(false);
                view.getView().getUndefinedCheckBox().setEnabled(false);
            }

            ExecNohrQueryAction action = new ExecNohrQueryAction(projectId, queryEditor.getValue(), showTrueAnswersCheckBox.getValue(), showUndefinedAnswersCheckBox.getValue(), showInconsistentAnswersCheckBox.getValue());
            dsm.execute(action
                    , result -> {
                        handleGetAnswers(result.getVariables(), result.getAnswers(), result.getCode());
                        setBusy(false);
                        for (NoHRQueryPresenter view : nohrQueriesProvider.getViews()) {
                            view.getView().getExecQueryButton().setEnabled(true);
                            view.getView().getTrueCheckBox().setEnabled(view.getView().getUndefinedCheckBox().getValue() || view.getView().getInconsistentCheckBox().getValue());
                            view.getView().getUndefinedCheckBox().setEnabled(view.getView().getTrueCheckBox().getValue() || view.getView().getInconsistentCheckBox().getValue());
                            view.getView().getInconsistentCheckBox().setEnabled(view.getView().getTrueCheckBox().getValue() || view.getView().getUndefinedCheckBox().getValue());
                        }
                    });
        }
    }

    @Override
    public Button getExecQueryButton() {
        return execQueryButton;
    }

    @Override
    public CheckBox getTrueCheckBox() {
        return showTrueAnswersCheckBox;
    }

    @Override
    public CheckBox getInconsistentCheckBox() {
        return showInconsistentAnswersCheckBox;
    }

    @Override
    public CheckBox getUndefinedCheckBox() {
        return showUndefinedAnswersCheckBox;
    }

    private void resetGrid() {

        for (int i = 0; i < lastColumnsNum; i++) {
            answersGrid.setColumnWidth(i, 0, Style.Unit.PCT);
        }

        for (Column c : columnsList) {
            answersGrid.clearColumnWidth(c);
            answersGrid.removeColumn(c);
        }

        columnsList.clear();
        lastColumnsNum = 2;

        Column trustVal = new TrustValCol();
        answersGrid.addColumn(trustVal, "Truth Values");
        columnsList.add(trustVal);
        answersGrid.getColumn(0).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        answersGrid.setColumnWidth(0, 50, Style.Unit.PCT);

        Column varCol = new VariableCol(0);
        answersGrid.addColumn(varCol, "Variables");
        columnsList.add(varCol);
        answersGrid.getColumn(1).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
        answersGrid.setColumnWidth(1, 50, Style.Unit.PCT);

        if (!answersGridProvider.getDataDisplays().contains(answersGrid))
            answersGridProvider.addDataDisplay(answersGrid);

        answersGridProvider.setList(new LinkedList<>());
        answersGridProvider.removeDataDisplay(answersGrid);
    }

    private void handleGetAnswers(List<String> variables, List<NohrUIAnswer> gridAnswers, NohrResponseCodes code) {

        switch (code) {
            case OK:

                for (int i = 0; i < lastColumnsNum; i++) {
                    answersGrid.setColumnWidth(i, 0, Style.Unit.PCT);
                }

                for (Column c : columnsList) {
                    answersGrid.clearColumnWidth(c);
                    answersGrid.removeColumn(c);
                }

                columnsList.clear();

                int colsNumber = lastColumnsNum = variables.size() + 1;
                double pct = 100 / colsNumber;

                if (gridAnswers.isEmpty()) {
                    pct = 100;
                    lastColumnsNum = 1;
                }

                Column trustVal = new TrustValCol();
                answersGrid.addColumn(trustVal, "");
                columnsList.add(trustVal);
                answersGrid.getColumn(0).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
                answersGrid.setColumnWidth(0, pct, Style.Unit.PCT);

                List<AnswersTable> answers = new LinkedList<>();
                if (gridAnswers.isEmpty())
                    answers.add(new AnswersTable("no answers found", new LinkedList<>()));
                else {
                    for (int i = 0; i < variables.size(); i++) {
                        Column varCol = new VariableCol(i);
                        answersGrid.addColumn(varCol, variables.get(i));
                        columnsList.add(varCol);
                        answersGrid.getColumn(i + 1).setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);
                        answersGrid.setColumnWidth(i + 1, pct, Style.Unit.PCT);
                    }
                }

                if (!answersGridProvider.getDataDisplays().contains(answersGrid))
                    answersGridProvider.addDataDisplay(answersGrid);

                for (int i = 0; i < gridAnswers.size(); i++) {
                    AnswersTable tmp = new AnswersTable(gridAnswers.get(i).getTruthValue(), gridAnswers.get(i).getValues());
                    answers.add(tmp);
                }

                answersGridProvider.setList(answers);
                answersGridProvider.removeDataDisplay(answersGrid);
                break;
            case PARSER_ERROR:
                errorMessageDisplay.displayQueryParserErrorMessage("Query syntax is not correct");
                break;
            case UNSUPPORTEDAXIOMS_ERROR:
                errorMessageDisplay.displayUnsupportedAxiomsErrorMessage("Query returned unsupported axioms");
                break;
            case PROLOGENGINE_ERROR:
                errorMessageDisplay.displayPrologEngineCreationErrorMessage("Prolog engine creation failed");
                break;
            case INCONSISTENTONTOLOGY_ERROR:
                errorMessageDisplay.displayInconsistentOntologyErrorMessage("Inconsistent ontology");
                break;
            case QUERYEXECUTING_ERROR:
                errorMessageDisplay.displayQueryExecutingErrorMessage("A query is running on this project");
                break;
            case UNKNOWN_ERROR:
                errorMessageDisplay.displayQueryParserErrorMessage("Unknown Error");
                break;
        }
    }

    @UiHandler({"showTrueAnswersCheckBox", "showUndefinedAnswersCheckBox", "showInconsistentAnswersCheckBox"})
    public void onCheckBoxesChangeEvent(ClickEvent event) {
        if (!queryEditor.getText().isEmpty()) {
            setBusy(true);
            busyView.setMessage("Executing Query");
            for (NoHRQueryPresenter view : nohrQueriesProvider.getViews()) {
                view.getView().getExecQueryButton().setEnabled(false);
                view.getView().getTrueCheckBox().setEnabled(false);
                view.getView().getInconsistentCheckBox().setEnabled(false);
                view.getView().getUndefinedCheckBox().setEnabled(false);
            }

            ExecNohrQueryAction action = new ExecNohrQueryAction(projectId, queryEditor.getValue(), showTrueAnswersCheckBox.getValue(), showUndefinedAnswersCheckBox.getValue(), showInconsistentAnswersCheckBox.getValue());
            dsm.execute(action
                    , result -> {
                        handleGetAnswers(result.getVariables(), result.getAnswers(), result.getCode());
                        setBusy(false);
                        for (NoHRQueryPresenter view : nohrQueriesProvider.getViews()) {
                            view.getView().getExecQueryButton().setEnabled(true);
                            view.getView().getTrueCheckBox().setEnabled(view.getView().getUndefinedCheckBox().getValue() || view.getView().getInconsistentCheckBox().getValue());
                            view.getView().getUndefinedCheckBox().setEnabled(view.getView().getTrueCheckBox().getValue() || view.getView().getInconsistentCheckBox().getValue());
                            view.getView().getInconsistentCheckBox().setEnabled(view.getView().getTrueCheckBox().getValue() || view.getView().getUndefinedCheckBox().getValue());
                        }
                    });
        }
    }

    @UiHandler("queryEditor")
    public void onChangeEvent(NohrRuleChangeEvent event) {

    }
}