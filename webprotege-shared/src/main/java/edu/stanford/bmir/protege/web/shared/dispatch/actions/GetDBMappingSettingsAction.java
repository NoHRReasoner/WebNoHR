package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetDBMappingSettingsAction extends AbstractNohrAction<GetDBMappingSettingsResult, NohrDatabaseSettings> {

    private GetDBMappingSettingsAction() {
    }

    public GetDBMappingSettingsAction(@Nonnull ProjectId projectId) {
        super(projectId);

    }

    @Override
    public String toString() {
        return toStringHelper("GetDBMappingSettingsAction")
                .toString();
    }
}
