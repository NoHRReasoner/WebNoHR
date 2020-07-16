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
public class UpdateDBMappingResult extends AbstractNohrResult<NohrDBMapping> {

    private NohrDBMapping newDBMapping;

    private NohrDBMapping newUIMapping;

    private NohrDBMapping oldDBMapping;

    private NohrDBMapping oldUIMapping;

    private NohrResponseCodes code;

    private UpdateDBMappingResult() {
    }

    public UpdateDBMappingResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, NohrDBMapping oldDBMapping, NohrDBMapping newDBMapping, NohrDBMapping oldUIMapping, NohrDBMapping newUIMapping, NohrResponseCodes code) {
        super(projectId, eventList);
        this.newDBMapping = newDBMapping;
        this.oldDBMapping = oldDBMapping;
        this.newUIMapping = newUIMapping;
        this.oldUIMapping = oldUIMapping;
        this.code = code;
    }

    public NohrDBMapping getNewDBMapping() {
        return newDBMapping;
    }

    public NohrDBMapping getOldDBMapping() {
        return oldDBMapping;
    }

    public NohrDBMapping getNewUIMapping() {
        return newUIMapping;
    }

    public NohrDBMapping getOldUIMapping() {
        return oldUIMapping;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}