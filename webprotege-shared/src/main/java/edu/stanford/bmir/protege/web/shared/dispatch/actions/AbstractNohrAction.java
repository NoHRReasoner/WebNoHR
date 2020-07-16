package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;
import edu.stanford.bmir.protege.web.shared.dispatch.AbstractHasProjectAction;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public abstract class AbstractNohrAction<R extends AbstractNohrResult<E>, E> extends AbstractHasProjectAction<R> {

    public AbstractNohrAction(@Nonnull ProjectId projectId) {
        super(projectId);

    }

    @GwtSerializationConstructor
    protected AbstractNohrAction() {
    }

}
