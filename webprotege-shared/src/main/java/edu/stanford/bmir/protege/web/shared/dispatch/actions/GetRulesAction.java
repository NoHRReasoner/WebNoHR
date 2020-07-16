package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetRulesAction extends AbstractNohrAction<GetRulesResult, NohrRule> {

    private GetRulesAction() {
    }

    public GetRulesAction(@Nonnull ProjectId projectId) {
        super(projectId);

    }

    @Override
    public String toString() {
        return toStringHelper("GetRulesAction")
                .toString();
    }
}
