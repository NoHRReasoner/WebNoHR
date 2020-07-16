package edu.stanford.bmir.protege.web.shared.projectsettings;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import edu.stanford.bmir.protege.web.shared.dispatch.AbstractHasProjectAction;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;

import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 25/11/14
 */
public class SetProjectSettingsAction extends AbstractHasProjectAction<SetProjectSettingsResult> {

    private ProjectSettings projectSettings;

    private NohrSettings nohrSettings;

    private List<NohrDatabaseSettings> nohrDatabaseSettings;

    /**
     * For serialization purposes only
     */
    private SetProjectSettingsAction() {
    }

    public SetProjectSettingsAction(ProjectSettings projectSettings, NohrSettings nohrSettings, List<NohrDatabaseSettings> nohrDatabaseSettings) {
        super(projectSettings.getProjectId());
        this.projectSettings = projectSettings;
        this.nohrSettings = nohrSettings;
        this.nohrDatabaseSettings = nohrDatabaseSettings;
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
        if (!(obj instanceof SetProjectSettingsAction)) {
            return false;
        }
        SetProjectSettingsAction other = (SetProjectSettingsAction) obj;
        return this.projectSettings.equals(other.projectSettings);
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper("SetProjectSettingsAction")
                .addValue(projectSettings)
                .toString();
    }
}
