package edu.stanford.bmir.protege.web.server.project;

import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.lang.ActiveLanguagesManager;
import edu.stanford.bmir.protege.web.server.nohrdata.NohrRepository;
import edu.stanford.bmir.protege.web.shared.lang.DictionaryLanguageUsage;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;
import edu.stanford.bmir.protege.web.shared.project.GetProjectInfoAction;
import edu.stanford.bmir.protege.web.shared.project.GetProjectInfoResult;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettings;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2018
 */
public class GetProjectInfoActionHandler extends AbstractProjectActionHandler<GetProjectInfoAction, GetProjectInfoResult> {

    @Nonnull
    private final ProjectDetailsManager projectDetailsManager;

    @Nonnull
    private final ActiveLanguagesManager activeLanguagesManager;

    @Nonnull
    private final NohrRepository nohrRepository;

    @Inject
    public GetProjectInfoActionHandler(@Nonnull AccessManager accessManager,
                                       @Nonnull ProjectDetailsManager projectDetailsManager,
                                       @Nonnull ActiveLanguagesManager activeLanguagesManager,
                                       @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectDetailsManager = checkNotNull(projectDetailsManager);
        this.activeLanguagesManager = checkNotNull(activeLanguagesManager);
        this.nohrRepository = checkNotNull(nohrRepository);
    }

    @Nonnull
    @Override
    public Class<GetProjectInfoAction> getActionClass() {
        return GetProjectInfoAction.class;
    }

    @Nonnull
    @Override
    public GetProjectInfoResult execute(@Nonnull GetProjectInfoAction action, @Nonnull ExecutionContext executionContext) {
        ProjectId projectId = action.getProjectId();
        ProjectSettings projectSettings = projectDetailsManager.getProjectSettings(projectId);
        ImmutableList<DictionaryLanguageUsage> languageUsage = activeLanguagesManager.getLanguageUsage();
        NohrSettings nohrSettings = nohrRepository.getSettings(projectId);
        List<NohrDatabaseSettings> nohrDatabaseSettings = nohrRepository.getDatabaseSettings(projectId);
        return GetProjectInfoResult.get(projectSettings, languageUsage, nohrSettings, nohrDatabaseSettings);
    }
}
