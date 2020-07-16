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
public class ExecNohrQueryAction extends AbstractNohrAction<ExecNohrQueryResult, NohrRule> {

    private String sourceText;

    private boolean trueAnswers;
    private boolean undefinedAnswers;
    private boolean inconsistentAnswers;

    private ExecNohrQueryAction() {
    }

    public ExecNohrQueryAction(@Nonnull ProjectId projectId,
                               @Nonnull String sourceText, boolean trueAnswers, boolean undefinedAnswers, boolean inconsistentAnswers) {
        super(projectId);
        System.out.println("criou a action");
        /*this.ontologyID = checkNotNull(ontologyID);*/
        this.sourceText = checkNotNull(sourceText);
        this.trueAnswers = trueAnswers;
        this.inconsistentAnswers = inconsistentAnswers;
        this.undefinedAnswers = undefinedAnswers;
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

    public boolean getTrueAnswers() {
        return trueAnswers;
    }

    public boolean getUndefinedAnswers() {
        return undefinedAnswers;
    }

    public boolean getInconsistentAnswers() {
        return inconsistentAnswers;
    }
}



