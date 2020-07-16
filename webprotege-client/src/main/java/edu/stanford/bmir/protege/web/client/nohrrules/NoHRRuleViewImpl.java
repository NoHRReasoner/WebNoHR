package edu.stanford.bmir.protege.web.client.nohrrules;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.list.ListBox;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UploadRulesPresenter;
import edu.stanford.bmir.protege.web.client.progress.BusyView;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NoHRRuleViewImpl extends Composite implements NoHRRuleView {

    private NohrRuleListCellRenderer renderer;

    interface NoHRRuleViewImplUiBinder extends UiBinder<HTMLPanel, NoHRRuleViewImpl> {

    }

    private static NoHRRuleViewImplUiBinder ourUiBinder = GWT.create(NoHRRuleViewImplUiBinder.class);

    @Nonnull
    private final UploadRulesPresenter uploadRulesPresenter;

    @UiField
    protected ListBox<String, NohrRule> rulesList;

    @UiField
    protected BusyView busyView;

    @Nonnull
    private final ProjectId projectId;

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Inject
    public NoHRRuleViewImpl(@Nonnull NohrRuleListCellRenderer renderer, @Nonnull UploadRulesPresenter uploadRulesPresenter, @Nonnull DispatchErrorMessageDisplay errorMessageDisplay, @Nonnull ProjectId projectId) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.uploadRulesPresenter = uploadRulesPresenter;
        this.errorMessageDisplay = errorMessageDisplay;
        this.renderer = checkNotNull(renderer);
        this.projectId = projectId;
        this.rulesList.setRenderer(renderer);
        this.rulesList.setKeyExtractor(rule -> rule.getRule());
        /*this.downloadButton.setEnabled(true);
        this.uploadButton.setEnabled(true);*/
    }

    @Override
    public void setBusy(boolean busy) {
        GWT.log("Set Busy: " + busy);
        busyView.setVisible(busy);
    }

    @Override
    public void setDisplayLanguage(@Nonnull DisplayNameSettings language) {
        renderer.setDisplayLanguage(language);
        rulesList.setRenderer(renderer);
    }

    @Override
    public void updateNode(@Nonnull NohrRule entityNode) {
        rulesList.updateElement(entityNode);
    }

    @Override
    public void setListData(List<NohrRule> rules) {
        rulesList.setListData(rules);
    }

    @Override
    public List<NohrRule> getListData() {
        return rulesList.getElements();
    }

    @Override
    public void addListData(NohrRule rule) {
        List<NohrRule> elements = rulesList.getElements();
        elements.add(rule);
        rulesList.setListData(elements);
    }

    @Override
    public void removeListData(Collection<NohrRule> rules) {
        List<NohrRule> elements = rulesList.getElements();
        elements.removeAll(rules);
        rulesList.setListData(elements);
    }

    @Override
    public void updateListData(NohrRule newRule, NohrRule oldRule) {
        List<NohrRule> elements = rulesList.getElements();
        elements.set(elements.indexOf(oldRule),newRule);
        rulesList.setListData(elements);
    }

    @Override
    public Collection<NohrRule> getSelectedRules() {
        return rulesList.getSelection();
    }

    @Override
    public Optional<NohrRule> getSelectedRule() {
        return rulesList.getFirstSelectedElement();
    }

    @Override
    public void setSelectedRule(NohrRule rule) {
        rulesList.setSelection(rule.getRule());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<NohrRule>> handler) {
        return rulesList.addSelectionHandler(handler);
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return rulesList.addDoubleClickHandler(handler);
    }
}
