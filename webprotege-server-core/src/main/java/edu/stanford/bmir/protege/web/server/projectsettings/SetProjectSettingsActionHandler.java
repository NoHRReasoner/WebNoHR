package edu.stanford.bmir.protege.web.server.projectsettings;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.nohrdata.NohrRepository;
import edu.stanford.bmir.protege.web.server.nohrdata.UsersNohrInstances;
import edu.stanford.bmir.protege.web.server.project.ProjectDetailsManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;
import edu.stanford.bmir.protege.web.shared.projectsettings.SetProjectSettingsAction;
import edu.stanford.bmir.protege.web.shared.projectsettings.SetProjectSettingsResult;
import pt.unl.fct.di.novalincs.nohr.plugin.NoHRInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import java.util.List;

import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_PROJECT_SETTINGS;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25/11/14
 */
public class SetProjectSettingsActionHandler extends AbstractProjectActionHandler<SetProjectSettingsAction, SetProjectSettingsResult> {

    @Nonnull
    private final ProjectDetailsManager projectDetailsManager;

    @Nonnull
    private final NohrRepository nohrRepository;

    @Nonnull
    private final UsersNohrInstances usersNohrInstances;

    @Inject
    public SetProjectSettingsActionHandler(@Nonnull AccessManager accessManager,
                                           @Nonnull ProjectDetailsManager projectDetailsManager,
                                           @Nonnull NohrRepository nohrRepository,
                                           @Nonnull UsersNohrInstances usersNohrInstances) {
        super(accessManager);
        this.projectDetailsManager = projectDetailsManager;
        this.nohrRepository = nohrRepository;
        this.usersNohrInstances = usersNohrInstances;
    }

    @Nonnull
    @Override
    public Class<SetProjectSettingsAction> getActionClass() {
        return SetProjectSettingsAction.class;
    }

    @Nullable
    @Override
    protected BuiltInAction getRequiredExecutableBuiltInAction() {
        return EDIT_PROJECT_SETTINGS;
    }

    @Nonnull
    @Override
    public SetProjectSettingsResult execute(@Nonnull SetProjectSettingsAction action, @Nonnull ExecutionContext executionContext) {
        projectDetailsManager.setProjectSettings(action.getProjectSettings());
        NohrSettings savedSettings = nohrRepository.getSettings(action.getProjectId());
        if (!savedSettings.equals(action.getNohrSettings()))
            usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(),action.getProjectId());
        nohrRepository.setSettings(action.getProjectId(),action.getNohrSettings());
        nohrRepository.setDatabaseSettings(action.getProjectId(),action.getNohrDatabaseSettings());
        List<NohrDatabaseSettings> allNohrDatabaseSettings = nohrRepository.getDatabaseSettings(action.getProjectId());
        return new SetProjectSettingsResult(projectDetailsManager.getProjectSettings(action.getProjectId()), action.getNohrSettings(), allNohrDatabaseSettings);
    }
}
