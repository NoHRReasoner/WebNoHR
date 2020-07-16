package edu.stanford.bmir.protege.web.shared.dispatch.actions;

import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrquery.NohrUIAnswer;
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
public class ExecNohrQueryResult extends AbstractNohrResult<NohrRule> {

    private List<NohrUIAnswer> answers;

    private List<String> variables;

    private NohrResponseCodes code;

    private ExecNohrQueryResult() {
    }

    public ExecNohrQueryResult(@Nonnull ProjectId projectId, @Nonnull EventList<ProjectEvent<?>> eventList, List<NohrUIAnswer> answers, List<String> variables, NohrResponseCodes code) {
        super(projectId, eventList);
        this.answers = answers;
        this.variables = variables;
        this.code = code;
    }

    /*public List<Answer> getAnswers() {
        return answers;
    }*/

    public List<NohrUIAnswer> getAnswers() {
        return answers;
    }

    public List<String> getVariables() {
        return variables;
    }

    public NohrResponseCodes getCode() {
        return code;
    }

}