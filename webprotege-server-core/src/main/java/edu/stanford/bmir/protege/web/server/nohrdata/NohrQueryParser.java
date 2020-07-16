package edu.stanford.bmir.protege.web.server.nohrdata;

import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.semanticweb.owlapi.model.OWLOntology;
import pt.unl.fct.di.novalincs.nohr.model.Query;
import pt.unl.fct.di.novalincs.nohr.plugin.query.QueryExpressionChecker;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrQueryParser extends NohrParser {

    QueryExpressionChecker checker;

    public NohrQueryParser() {
        super();
        checker = new QueryExpressionChecker(getParser());
    }

    public NohrQueryParser(OWLOntology ontology) {
        super(ontology);
        checker = new QueryExpressionChecker(getParser());
    }

    public void checkQuery(String str) throws OWLExpressionParserException {
        checker.check(str);
    }

    public Query createQuery(String str) throws OWLExpressionParserException {
        return checker.createObject(str);
    }
}
