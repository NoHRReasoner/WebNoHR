package edu.stanford.bmir.protege.web.server.nohrdata;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.model.Rule;
import pt.unl.fct.di.novalincs.nohr.plugin.rules.RuleExpressionChecker;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrRuleParser extends NohrParser {

    RuleExpressionChecker checker;

    public NohrRuleParser() {
        super();
        checker = new RuleExpressionChecker(getParser());
    }

    public NohrRuleParser(OWLOntology ontology) {
        super(ontology);
        checker = new RuleExpressionChecker(getParser());
    }

    public void checkRule(String str) throws OWLExpressionParserException {
        checker.check(str);
    }
    public Rule createRule(String str) throws OWLExpressionParserException {
        return checker.createObject(str);
    }
}
