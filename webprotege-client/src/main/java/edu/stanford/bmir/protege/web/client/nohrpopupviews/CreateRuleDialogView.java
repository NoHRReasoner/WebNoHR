package edu.stanford.bmir.protege.web.client.nohrpopupviews;

import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.library.dlg.HasInitialFocusable;
import org.semanticweb.owlapi.model.EntityType;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface CreateRuleDialogView extends IsWidget, HasInitialFocusable {

    interface ResetLangTagHandler {
        void handleResetLangTag();
    }

    interface LangTagChangedHandler {
        void handleLangTagChanged();
    }

    void setEntityType(@Nonnull EntityType<?> entityType);

    @Nonnull
    String getText();

    void clear();

}
