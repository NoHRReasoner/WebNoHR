package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetDBMappingAction extends AbstractNohrAction<GetDBMappingResult, NohrDBMapping> {

    private GetDBMappingAction() {
    }

    public GetDBMappingAction(@Nonnull ProjectId projectId) {
        super(projectId);

    }

    @Override
    public String toString() {
        return toStringHelper("GetDBMappingAction")
                .toString();
    }
}
