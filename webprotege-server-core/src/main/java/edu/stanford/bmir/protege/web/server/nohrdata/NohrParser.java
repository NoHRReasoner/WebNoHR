package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.owlapi.WebProtegeOWLManager;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.DefaultVocabulary;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRParser;
import pt.unl.fct.di.novalincs.nohr.parsing.NoHRRecursiveDescentParser;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public abstract class NohrParser {

    OWLOntologyManager ontologyManager;
    OWLOntologyMerger merger;
    OWLOntology ontology;
    Vocabulary vocabulary;
    NoHRParser parser;

    public NohrParser() {
        ontologyManager = WebProtegeOWLManager.createOWLOntologyManager();
        merger = new OWLOntologyMerger(ontologyManager);

        try {
            ontology = merger.createMergedOntology(ontologyManager, IRI.generateDocumentIRI());
        } catch (OWLOntologyCreationException e) {
            e.printStackTrace();
        }

        vocabulary = new DefaultVocabulary(ontology);
        parser = new NoHRRecursiveDescentParser(vocabulary);
    }

    public NohrParser(OWLOntology ontology) {
        this.ontology = ontology;
        vocabulary = new DefaultVocabulary(ontology);
        parser = new NoHRRecursiveDescentParser(vocabulary);
    }

    public NoHRParser getParser() {
        return parser;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public void setOntology(OWLOntology ontology) {
        this.ontology = ontology;
        vocabulary = new DefaultVocabulary(ontology);
        parser = new NoHRRecursiveDescentParser(vocabulary);
    }

    public Vocabulary getVocabulary() {
        return vocabulary;
    }
}
