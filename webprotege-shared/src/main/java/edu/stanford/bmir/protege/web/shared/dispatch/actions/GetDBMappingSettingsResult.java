package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetDBMappingSettingsResult extends AbstractNohrResult<NohrDatabaseSettings> {

    private List<NohrDatabaseSettings> dbMappingsSettings;

    private NohrResponseCodes code;

    private GetDBMappingSettingsResult() {
    }

    public GetDBMappingSettingsResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, List<NohrDatabaseSettings> dbMappingsSettings, NohrResponseCodes code) {
        super(projectId,eventList);
        this.dbMappingsSettings = dbMappingsSettings;
        this.code = code;

    }

    public List<NohrDatabaseSettings> getDbMappings() {
        return dbMappingsSettings;
    }

    public NohrResponseCodes getCode() {
        return code;
    }

}
