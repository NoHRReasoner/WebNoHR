package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingSettingsAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingSettingsResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.GET_DBMAPPING_SETTINGS;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.VIEW_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class GetDBMappingSettingsActionHandler extends AbstractProjectActionHandler<GetDBMappingSettingsAction, GetDBMappingSettingsResult> {

    private static final Logger logger = LoggerFactory.getLogger(GetDBMappingSettingsActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    @Inject
    public GetDBMappingSettingsActionHandler(@Nonnull AccessManager accessManager,
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
        return Arrays.asList(VIEW_NOHR, GET_DBMAPPING_SETTINGS);
    }

    @Nonnull
    @Override
    public GetDBMappingSettingsResult execute(@Nonnull GetDBMappingSettingsAction action,
                                              @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        List<NohrDatabaseSettings> databaseSettings = new LinkedList<>();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);

       try {
           databaseSettings = repository.getDatabaseSettings(projectId);
       } catch (Exception e) {
           logger.info("Failed to get database mappings settings from database to {}, meaning project have no database mappings settings added",projectId);
           System.out.println("Failed to get database mappings settings from database to "+projectId+", meaning project have no database mappings settings added");
           return new GetDBMappingSettingsResult(projectId,
                   eventList,
                   databaseSettings, NohrResponseCodes.DATABASE_ERROR);
       }


        return new GetDBMappingSettingsResult(projectId,
                eventList,
                databaseSettings, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<GetDBMappingSettingsAction> getActionClass() {
        return GetDBMappingSettingsAction.class;
    }


}
