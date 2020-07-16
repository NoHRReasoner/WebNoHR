package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
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
public class UploadDBMappingAction extends AbstractNohrAction<UploadDBMappingResult, NohrDBMapping> {

    private String file;

    private UploadDBMappingAction() {
    }

    public UploadDBMappingAction(@Nonnull ProjectId projectId,
                                 @Nonnull String file) {
        super(projectId);
        this.file = checkNotNull(file);

    }

    @Override
    public String toString() {
        return toStringHelper("UploadDBMappingAction")
                .add("sourceText", getFile())
                .toString();
    }

    public String getFile() {
        return file;
    }

}



