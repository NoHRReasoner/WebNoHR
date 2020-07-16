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
public class CreateRulesAction extends AbstractNohrAction<CreateRulesResult, NohrRule> {

    private String sourceText;

    private CreateRulesAction() {
    }

    public CreateRulesAction(@Nonnull ProjectId projectId,
                                        @Nonnull String sourceText) {
        super(projectId);
        this.sourceText = checkNotNull(sourceText);

    }

    @Override
    public String toString() {
        return toStringHelper("CreateRulesAction")
                .add("sourceText", getSourceText())
                .toString();
    }

    public String getSourceText() {
        return sourceText;
    }

}



