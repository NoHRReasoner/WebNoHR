package edu.stanford.bmir.protege.web.shared.projectsettings;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import edu.stanford.bmir.protege.web.shared.dispatch.Result;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;

import javax.annotation.Nonnull;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25/11/14
 */
public class SetProjectSettingsResult implements Result {

    private ProjectSettings projectSettings;

    private NohrSettings nohrSettings;

    private List<NohrDatabaseSettings> nohrDatabaseSettings;

    private SetProjectSettingsResult() {

    }

    public SetProjectSettingsResult(@Nonnull ProjectSettings projectSettings, @Nonnull NohrSettings nohrSettings, @Nonnull List<NohrDatabaseSettings> nohrDatabaseSettings) {
        this.projectSettings = checkNotNull(projectSettings);
        this.nohrSettings = checkNotNull(nohrSettings);
        this.nohrDatabaseSettings = checkNotNull(nohrDatabaseSettings);
    }

    public ProjectSettings getProjectSettings() {
        return projectSettings;
    }

    public NohrSettings getNohrSettings() {
        return nohrSettings;
    }

    public List<NohrDatabaseSettings> getNohrDatabaseSettings() {
        return nohrDatabaseSettings;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectSettings);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SetProjectSettingsResult)) {
            return false;
        }
        SetProjectSettingsResult other = (SetProjectSettingsResult) obj;
        return this.getProjectSettings().equals(other.getProjectSettings());
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper("SetProjectSettingsResult")
                .addValue(projectSettings)
                .toString();
    }
}
