package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class CreateDBMappingResult extends AbstractNohrResult<NohrDBMapping> {

    private NohrDBMapping dbMapping;

    private NohrDBMapping uiDBMapping;

    private NohrResponseCodes code;

    private CreateDBMappingResult() {
    }

    public CreateDBMappingResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, NohrDBMapping dbMapping, NohrDBMapping uiDBMapping, NohrResponseCodes code) {
        super(projectId, eventList);
        this.dbMapping = dbMapping;
        this.uiDBMapping = uiDBMapping;
        this.code = code;
    }

    public NohrDBMapping getDbMapping() {
        return dbMapping;
    }

    public NohrDBMapping getUIDbMapping() {
        return uiDBMapping;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}