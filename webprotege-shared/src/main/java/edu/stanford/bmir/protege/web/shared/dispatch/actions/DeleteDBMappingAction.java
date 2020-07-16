package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Collection;

import static com.google.common.base.MoreObjects.toStringHelper;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class DeleteDBMappingAction extends AbstractNohrAction<DeleteDBMappingResult, NohrDBMapping> {

    Collection<NohrDBMapping> dbMappings;
    Collection<NohrDBMapping> uiMappings;

    private DeleteDBMappingAction() {
    }

    public DeleteDBMappingAction(@Nonnull ProjectId projectId, Collection<NohrDBMapping> dbMappings, Collection<NohrDBMapping> uiMappings) {
        super(projectId);
        this.dbMappings = dbMappings;
        this.uiMappings = uiMappings;

    }

    @Override
    public String toString() {
        return toStringHelper("DeleteDBMappingAction")
                .toString();
    }

    public Collection<NohrDBMapping> getDbMappings() {
        return dbMappings;
    }

    public Collection<NohrDBMapping> getUiMappings() {
        return uiMappings;
    }
}
