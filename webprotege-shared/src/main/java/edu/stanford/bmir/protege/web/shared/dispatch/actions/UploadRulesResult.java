package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class UploadRulesResult extends AbstractNohrResult<NohrRule> {

    private List<NohrRule> rules;

    private NohrResponseCodes code;

    private Integer lineNumber;

    private UploadRulesResult() {
    }

    public UploadRulesResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, List<NohrRule> rules, Integer linenumber, NohrResponseCodes code) {
        super(projectId,eventList);
        this.rules = rules;
        this.lineNumber =linenumber;
        this.code = code;

    }

    public List<NohrRule> getRules() {
        return rules;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}