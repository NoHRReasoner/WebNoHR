package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetRulesAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetRulesResult;
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
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.GET_RULE;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.VIEW_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class GetRulesActionHandler extends AbstractProjectActionHandler<GetRulesAction, GetRulesResult> {

    private static final Logger logger = LoggerFactory.getLogger(GetRulesActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    @Inject
    public GetRulesActionHandler(@Nonnull AccessManager accessManager,
                                 @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull HasApplyChanges changeApplicator,
                                 @Nonnull EntityNodeRenderer renderer, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.repository = checkNotNull(nohrRepository);
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(VIEW_NOHR, GET_RULE);
    }

    @Nonnull
    @Override
    public GetRulesResult execute(@Nonnull GetRulesAction action,
                                  @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        List<NohrRule> rules = new LinkedList<>();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);

       try {
           rules = repository.getRules(projectId);
       } catch (Exception e) {
           logger.info("Failed to get Rules from database to {}, meaning project have no rules added",projectId);
           System.out.println("Failed to get Rules from database to "+projectId+", meaning project have no rules added");
           return new GetRulesResult(projectId,
                   eventList,
                   rules, NohrResponseCodes.DATABASE_ERROR);
       }


        return new GetRulesResult(projectId,
                eventList,
                rules, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<GetRulesAction> getActionClass() {
        return GetRulesAction.class;
    }


}
