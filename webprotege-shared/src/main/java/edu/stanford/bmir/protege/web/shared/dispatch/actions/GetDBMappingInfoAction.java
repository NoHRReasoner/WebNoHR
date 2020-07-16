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
public class GetDBMappingInfoAction extends AbstractNohrAction<GetDBMappingInfoResult, NohrDBMapping> {

    String dbMapping;

    private GetDBMappingInfoAction() {
    }

    public GetDBMappingInfoAction(@Nonnull ProjectId projectId, String dbMapping) {
        super(projectId);
        this.dbMapping = dbMapping;

    }

    @Override
    public String toString() {
        return toStringHelper("GetDBMappingAction")
                .toString();
    }

    public String getDbMapping() {
        return dbMapping;
    }
}
