package edu.stanford.bmir.protege.web.client.nohrrules;

import edu.stanford.bmir.protege.web.client.Messages;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameRenderer;
import edu.stanford.bmir.protege.web.client.lang.DisplayNameSettingsManager;
import edu.stanford.bmir.protege.web.client.portlet.AbstractWebProtegePortletPresenter;
import edu.stanford.bmir.protege.web.client.portlet.PortletUi;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.client.tag.TagVisibilityPresenter;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.webprotege.shared.annotations.Portlet;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */

@Portlet(id = "portlets.NoHRRules", title = "NoHR Rules")
public class NoHRRulePortletPresenter extends AbstractWebProtegePortletPresenter {

    private final NoHRRulePresenter presenter;

    private final DisplayNameSettingsManager displayNameSettingsManager;

    @Nonnull
    private final TagVisibilityPresenter tagVisibilityPresenter;

    @Nonnull
    private final Messages messages;

    private final NohrRulesProvider nohrRulesProvider;


    @Inject
    public NoHRRulePortletPresenter(@Nonnull NoHRRulePresenter presenter,
                                    @Nonnull SelectionModel selectionModel,
                                    @Nonnull ProjectId projectId,
                                    @Nonnull DisplayNameRenderer displayNameRenderer,
                                    @Nonnull DisplayNameSettingsManager displayNameSettingsManager,
                                    @Nonnull TagVisibilityPresenter tagVisibilityPresenter,
                                    @Nonnull Messages messages,
                                    @Nonnull NohrRulesProvider nohrRulesProvider) {
        super(selectionModel, projectId, displayNameRenderer);
        this.presenter = checkNotNull(presenter);
        this.displayNameSettingsManager = checkNotNull(displayNameSettingsManager);
        this.tagVisibilityPresenter = checkNotNull(tagVisibilityPresenter);
        this.messages = checkNotNull(messages);
        this.nohrRulesProvider = nohrRulesProvider;
        this.nohrRulesProvider.InsertView(presenter);
    }

    @Override
    public void startPortlet(PortletUi portletUi, WebProtegeEventBus eventBus) {
        presenter.installActions(portletUi);
        presenter.start(portletUi, eventBus);
        handleAfterSetEntity(getSelectedEntity());
    }

    @Override
    public void dispose() {
        nohrRulesProvider.removeView(presenter);
        super.dispose();
    }
}
