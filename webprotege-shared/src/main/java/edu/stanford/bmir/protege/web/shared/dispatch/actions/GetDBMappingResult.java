package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class GetDBMappingResult extends AbstractNohrResult<NohrDBMapping> {

    private List<NohrDBMapping> dbMappings;

    private List<NohrDBMapping> uiMappings;

    private NohrResponseCodes code;

    private GetDBMappingResult() {
    }

    public GetDBMappingResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, List<NohrDBMapping> dbMappings, List<NohrDBMapping> uiMappings, NohrResponseCodes code) {
        super(projectId,eventList);
        this.dbMappings = dbMappings;
        this.uiMappings = uiMappings;
        this.code = code;

    }

    public List<NohrDBMapping> getDbMappings() {
        return dbMappings;
    }

    public List<NohrDBMapping> getUiMappings() {
        return uiMappings;
    }

    public NohrResponseCodes getCode() {
        return code;
    }

}
