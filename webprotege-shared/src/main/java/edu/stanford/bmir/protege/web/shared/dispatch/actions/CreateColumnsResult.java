package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class CreateColumnsResult extends AbstractNohrResult<NohrColumnsTable> {

    private NohrColumnsTable row;

    private NohrResponseCodes code;

    private CreateColumnsResult() {
    }

    public CreateColumnsResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, NohrColumnsTable row, NohrResponseCodes code) {
        super(projectId, eventList);
        this.row = row;
        this.code = code;
    }

    public NohrColumnsTable getElement() {
        return row;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}