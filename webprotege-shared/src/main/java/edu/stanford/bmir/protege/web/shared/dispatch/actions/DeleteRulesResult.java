package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 * Date: 25/09/2019
 */
public class DeleteRulesResult extends AbstractNohrResult<NohrRule> {

    private Collection<NohrRule> rules;

    private NohrResponseCodes code;

    private DeleteRulesResult() {
    }

    public DeleteRulesResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, Collection<NohrRule> rules, NohrResponseCodes code) {
        super(projectId, eventList);
        this.rules = rules;
        this.code = code;
    }


    public Collection<NohrRule> getRules() {
        return rules;
    }

    public NohrResponseCodes getCode() {
        return code;
    }

}
