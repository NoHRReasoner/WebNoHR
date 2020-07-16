package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class UploadDBMappingResult extends AbstractNohrResult<NohrDBMapping> {

    private List<NohrDBMapping> dbMappings;

    private List<NohrDBMapping> uiMappings;

    private Integer lineNumber;

    private NohrResponseCodes code;

    private UploadDBMappingResult() {
    }

    public UploadDBMappingResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, List<NohrDBMapping> dbMappings, List<NohrDBMapping> uiMappings, Integer lineNumber, NohrResponseCodes code) {
        super(projectId,eventList);
        this.dbMappings = dbMappings;
        this.uiMappings = uiMappings;
        this.lineNumber = lineNumber;
        this.code = code;
    }

    public List<NohrDBMapping> getDBMappings() {
        return dbMappings;
    }

    public List<NohrDBMapping> getUIMappings() { return uiMappings;}

    public Integer getLineNumber() {
        return lineNumber;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}