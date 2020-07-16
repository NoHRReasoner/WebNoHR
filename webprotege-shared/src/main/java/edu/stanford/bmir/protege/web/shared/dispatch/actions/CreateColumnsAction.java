package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
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
public class CreateColumnsAction extends AbstractNohrAction<CreateColumnsResult, NohrColumnsTable> {

    private String sourceText;

    private CreateColumnsAction() {
    }

    public CreateColumnsAction(@Nonnull ProjectId projectId,
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



