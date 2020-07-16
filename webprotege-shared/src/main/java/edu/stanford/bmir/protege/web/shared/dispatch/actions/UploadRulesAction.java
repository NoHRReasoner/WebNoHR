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
public class UploadRulesAction extends AbstractNohrAction<UploadRulesResult, NohrRule> {

    private String file;

    private UploadRulesAction() {
    }

    public UploadRulesAction(@Nonnull ProjectId projectId,
                             @Nonnull String file) {
        super(projectId);
        this.file = checkNotNull(file);

    }

    @Override
    public String toString() {
        return toStringHelper("UploadRulesAction")
                .add("sourceText", getFile())
                .toString();
    }

    public String getFile() {
        return file;
    }

}



