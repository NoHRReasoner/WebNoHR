package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class UpdateRulesAction extends AbstractNohrAction<UpdateRulesResult, NohrRule> {

    private String newRule;

    private String oldRule;

    private UpdateRulesAction() {
    }

    public UpdateRulesAction(@Nonnull ProjectId projectId,
                             @Nonnull String newRule,
                             @Nonnull String oldRule) {
        super(projectId);
        this.newRule = checkNotNull(newRule);
        this.oldRule = checkNotNull(oldRule);

    }

    @Override
    public String toString() {
        return toStringHelper("UpdateRulesAction")
                .add("newRule", getNewRule())
                .add("oldRule", getOldRule())
                .toString();
    }

    public String getNewRule() {
        return newRule;
    }

    public String getOldRule() {
        return oldRule;
    }
}



