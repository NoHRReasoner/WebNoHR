package edu.stanford.bmir.protege.web.client.nohrrules;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */

public interface NoHRRuleView extends HasSelectionHandlers<List<NohrRule>>, HasBusy, IsWidget {

    void setDisplayLanguage(@Nonnull DisplayNameSettings language);

    void updateNode(@Nonnull NohrRule entityNode);

    void setListData(List<NohrRule> rules);

    Collection<NohrRule> getListData();

    void addListData(NohrRule rules);

    void updateListData(NohrRule newRule, NohrRule oldRule);

    void removeListData(Collection<NohrRule> rules);

    Collection<NohrRule> getSelectedRules();

    Optional<NohrRule> getSelectedRule();

    void setSelectedRule(NohrRule rule);

    HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler);
}
