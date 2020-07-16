package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class DeleteRulesAction extends AbstractNohrAction<DeleteRulesResult, NohrRule> {

    Collection<NohrRule> rules;

    private DeleteRulesAction() {
    }

    public DeleteRulesAction(@Nonnull ProjectId projectId, Collection<NohrRule> rules) {
        super(projectId);
        this.rules = rules;

    }

    @Override
    public String toString() {
        return toStringHelper("DeleteRulesAction")
                .toString();
    }

    public Collection<NohrRule> getRules() {
        return rules;
    }
}
