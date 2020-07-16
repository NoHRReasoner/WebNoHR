package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.HasEventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.project.HasProjectId;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public abstract class AbstractNohrResult<E> implements Result, HasProjectId, HasEventList<ProjectEvent<?>> {

    private EventList<ProjectEvent<?>> eventList;

    private ProjectId projectId;

    public AbstractNohrResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList) {
        this.eventList = checkNotNull(eventList);
        this.projectId = checkNotNull(projectId);

    }

    @GwtSerializationConstructor
    protected AbstractNohrResult() {
    }

    @Nonnull
    @Override
    public ProjectId getProjectId() {
        return projectId;
    }

    @Override
    public EventList<ProjectEvent<?>> getEventList() {
        return eventList;
    }

}
