package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.DeleteRulesAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.DeleteRulesResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.DELETE_RULE;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class DeleteRulesActionHandler extends AbstractProjectActionHandler<DeleteRulesAction, DeleteRulesResult> {

    private static final Logger logger = LoggerFactory.getLogger(DeleteRulesActionHandler.class);
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
    public DeleteRulesActionHandler(@Nonnull AccessManager accessManager,
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
        return Arrays.asList(EDIT_NOHR, DELETE_RULE);
    }

    @Nonnull
    @Override
    public DeleteRulesResult execute(@Nonnull DeleteRulesAction action,
                                     @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        Collection<NohrRule> rules = action.getRules();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
       try {
           repository.deleteRules(projectId,rules);
           usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(),projectId);
       } catch (Exception e) {
           logger.info("Failed to delete rules in database for {}",projectId);
           System.out.println("Failed to delete rules in database for " + projectId);
           return new DeleteRulesResult(projectId,
                   eventList,
                   rules, NohrResponseCodes.DATABASE_ERROR);
       }
        return new DeleteRulesResult(projectId,
                eventList,
                rules, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<DeleteRulesAction> getActionClass() {
        return DeleteRulesAction.class;
    }

}
