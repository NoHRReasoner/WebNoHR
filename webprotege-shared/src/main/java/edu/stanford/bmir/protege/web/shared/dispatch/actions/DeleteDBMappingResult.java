package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class DeleteDBMappingResult extends AbstractNohrResult<NohrDBMapping> {

    private Collection<NohrDBMapping> dbMappings;

    private Collection<NohrDBMapping> uiMappings;

    private NohrResponseCodes code;

    private DeleteDBMappingResult() {
    }

    public DeleteDBMappingResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, Collection<NohrDBMapping> dbMappings, Collection<NohrDBMapping> uiMappings, NohrResponseCodes code) {
        super(projectId, eventList);
        this.dbMappings = dbMappings;
        this.uiMappings = uiMappings;
        this.code = code;
    }


    public Collection<NohrDBMapping> getDbMappings() {
        return dbMappings;
    }

    public Collection<NohrDBMapping> getUiMappings() {
        return uiMappings;
    }

    public NohrResponseCodes getCode() {
        return code;
    }

}
