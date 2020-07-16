package edu.stanford.bmir.protege.web.client.nohrdbmappings;


import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.list.ListBoxCellRenderer;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrDBMappingsListCellRenderer implements ListBoxCellRenderer<NohrDBMapping> {

    @Nonnull
    private final NohrDBMappingsHtmlRenderer renderer;

    @Inject
    public NohrDBMappingsListCellRenderer(@Nonnull NohrDBMappingsHtmlRenderer renderer) {
        this.renderer = checkNotNull(renderer);
    }

    @Override
    public IsWidget render(NohrDBMapping element) {
        return new HTMLPanel(renderer.getHtmlRendering(element));
    }

    public void setDisplayLanguage(@Nonnull DisplayNameSettings displayLanguage) {
        renderer.setDisplayLanguage(displayLanguage);
    }
}
