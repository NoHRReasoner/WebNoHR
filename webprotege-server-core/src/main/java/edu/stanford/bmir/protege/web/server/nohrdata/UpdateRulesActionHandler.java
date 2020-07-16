package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateRulesAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateRulesResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRuleImpl;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_NOHR;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPDATE_RULE;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UpdateRulesActionHandler extends AbstractProjectActionHandler<UpdateRulesAction, UpdateRulesResult> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateRulesActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    private NohrRuleParser parser;

    private UsersNohrInstances usersNohrInstances;

    @Inject
    public UpdateRulesActionHandler(@Nonnull AccessManager accessManager,
                                    @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull HasApplyChanges changeApplicator,
                                    @Nonnull EntityNodeRenderer renderer, @Nonnull UsersNohrInstances usersNoHRInstances, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.repository = checkNotNull(nohrRepository);
        this.parser = new NohrRuleParser();
        this.usersNohrInstances = usersNoHRInstances;
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(EDIT_NOHR, UPDATE_RULE);
    }

    @Nonnull
    @Override
    public UpdateRulesResult execute(@Nonnull UpdateRulesAction action,
                                     @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        NohrRule newRule = new NohrRuleImpl(action.getNewRule());
        NohrRule oldRule = new NohrRuleImpl(action.getOldRule());
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
        try {
            parser.checkRule(newRule.getRule());
            if (!repository.getRules(projectId).contains(new NohrRuleImpl(action.getNewRule()))) {
                repository.updateRule(projectId, oldRule.getRule(), newRule.getRule());
                usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(), projectId);
            }
        } catch (OWLExpressionParserException e) {
            logger.info("Rule syntax is not correct in {}", projectId);
            System.out.println("Rule syntax is not correct in " + projectId);
            return new UpdateRulesResult(projectId,
                    eventList,
                    null,null, NohrResponseCodes.PARSER_ERROR);
        } catch (Exception e) {
            logger.info("Failed to update rule {} to database in {}",oldRule.getRule(),projectId);
            System.out.println("Failed to update rule "+oldRule.getRule()+" to database in "+projectId);
            return new UpdateRulesResult(projectId,
                    eventList,
                    null,null, NohrResponseCodes.DATABASE_ERROR);
        }

        return new UpdateRulesResult(projectId,
                eventList,
                oldRule, newRule, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<UpdateRulesAction> getActionClass() {
        return UpdateRulesAction.class;
    }
}
