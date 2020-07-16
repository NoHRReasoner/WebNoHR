package edu.stanford.bmir.protege.web.shared.project;

import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableList;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.lang.DictionaryLanguageUsage;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;
import edu.stanford.bmir.protege.web.shared.projectsettings.ProjectSettings;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 21 Aug 2018
 */
@AutoValue
@GwtCompatible(serializable = true)
public abstract class GetProjectInfoResult implements Result {

    public static GetProjectInfoResult get(@Nonnull ProjectSettings projectSettings,
                                           @Nonnull ImmutableList<DictionaryLanguageUsage> languageUsage,
                                           @Nonnull NohrSettings nohrSettings,
                                           @Nonnull List<NohrDatabaseSettings> nohrDatabaseSettings) {
        return new AutoValue_GetProjectInfoResult(projectSettings,
                languageUsage, nohrSettings, nohrDatabaseSettings);
    }

    @Nonnull
    public abstract ProjectSettings getProjectDetails();

    @Nonnull
    public abstract ImmutableList<DictionaryLanguageUsage> getProjectLanguages();

    @Nonnull
    public abstract NohrSettings getNohrSettings();

    public abstract List<NohrDatabaseSettings> getNohrDatabaseSettings();
}
