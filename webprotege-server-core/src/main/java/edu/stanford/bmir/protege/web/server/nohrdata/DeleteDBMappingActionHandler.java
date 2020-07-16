package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.DeleteDBMappingAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.DeleteDBMappingResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.DELETE_DBMAPPING;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class DeleteDBMappingActionHandler extends AbstractProjectActionHandler<DeleteDBMappingAction, DeleteDBMappingResult> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteDBMappingActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    private UsersNohrInstances usersNohrInstances;

    @Inject
    public DeleteDBMappingActionHandler(@Nonnull AccessManager accessManager,
                                        @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull HasApplyChanges changeApplicator,
                                        @Nonnull EntityNodeRenderer renderer,
                                        @Nonnull UsersNohrInstances usersNoHRInstances, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.repository = checkNotNull(nohrRepository);
        this.usersNohrInstances = usersNoHRInstances;
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(EDIT_NOHR, DELETE_DBMAPPING);
    }

    @Nonnull
    @Override
    public DeleteDBMappingResult execute(@Nonnull DeleteDBMappingAction action,
                                         @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        Collection<NohrDBMapping> dbMappings = action.getDbMappings();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
       try {
           repository.deleteDBMapping(projectId,dbMappings);
           usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(),projectId);
       } catch (Exception e) {
           logger.info("Failed to delete dbMappings in database for {}",projectId);
           System.out.println("Failed to delete dbMappings in database for " + projectId);
           return new DeleteDBMappingResult(projectId,
                   eventList,
                   null, null, NohrResponseCodes.DATABASE_ERROR);
       }
        return new DeleteDBMappingResult(projectId,
                eventList,
                dbMappings, action.getUiMappings(), NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<DeleteDBMappingAction> getActionClass() {
        return DeleteDBMappingAction.class;
    }

}
