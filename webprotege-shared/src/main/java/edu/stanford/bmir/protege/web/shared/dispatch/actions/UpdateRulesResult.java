package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class UpdateRulesResult extends AbstractNohrResult<NohrRule> {

    private NohrRule newRule;

    private NohrRule oldRule;

    private NohrResponseCodes code;

    private UpdateRulesResult() {
    }

    public UpdateRulesResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, NohrRule oldRule, NohrRule newRule, NohrResponseCodes code) {
        super(projectId, eventList);
        this.newRule = newRule;
        this.oldRule = oldRule;
        this.code = code;
    }

    public NohrRule getNewRule() {
        return newRule;
    }

    public NohrRule getOldRule() {
        return oldRule;
    }

    public NohrResponseCodes getCode() {
        return code;
    }
}