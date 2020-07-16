package pt.unl.fct.di.novalincs.nohr.plugin;

import org.semanticweb.owlapi.model.OWLOntology;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.OWLProfilesViolationsException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.DBMappingSet;
import pt.unl.fct.di.novalincs.nohr.model.Program;
import pt.unl.fct.di.novalincs.nohr.model.vocabulary.Vocabulary;

import java.util.HashSet;
import java.util.Set;

/*import static pt.unl.fct.di.novalincs.nohr.plugin.AbstractNoHRViewComponent.LOG;*/

public class NoHRInstance {

    private Logger logger = LoggerFactory.getLogger(NoHRInstance.class);

    private static NoHRInstance instance;

    private DisposableHybridKB hybridKB;
    private Set<NoHRInstanceChangedListener> listeners;

    public NoHRInstance() {
        listeners = new HashSet<>();
    }

    public void addListener(NoHRInstanceChangedListener listener) {
        listeners.add(listener);
    }

    public DisposableHybridKB getHybridKB() {
        return hybridKB;

    }

    public static NoHRInstance getInstance() {
        if (instance == null) {
            instance = new NoHRInstance();
        }

        return instance;
    }

    public boolean isStarted() {
        return hybridKB != null;
    }

    public void requestRestart() {
        for (NoHRInstanceChangedListener i : listeners) {
            i.instanceChanged(new NoHRInstanceChangedEventImpl(NoHRInstanceChangedEventType.REQUEST_RESTART));
        }
    }

    public void removeListener(NoHRInstanceChangedListener listener) {
        listeners.remove(listener);
    }

    public void restart() throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        if (!isStarted()) {
            return;
        }

        logger.info("Restarting NoHR");
        System.out.println("Restarting NoHR");

        final OWLOntology ontology = hybridKB.getOntology();
        final Program program = hybridKB.getProgram();
        final DBMappingSet dbMappingsSet = hybridKB.getDBMappings();
        final Vocabulary vocabulary = hybridKB.getVocabulary();

        stop();
        start(NoHRPreferences.getInstance().getConfiguration(), ontology, program, dbMappingsSet, vocabulary);
    }

    public void start(NoHRHybridKBConfiguration configuration, OWLOntology ontology, Program program,DBMappingSet dbMappingsSet, Vocabulary vocabulary) throws UnsupportedAxiomsException, OWLProfilesViolationsException, PrologEngineCreationException {
        logger.info("Starting NoHR");
        System.out.println("Starting NoHR");

        hybridKB = new DisposableHybridKB(configuration, ontology, program, dbMappingsSet,  vocabulary);
    }

    public void stop() {
        logger.info("Stopping NoHR");
        System.out.println("Stopping NoHR");

        if (isStarted()) {
            hybridKB.dispose();
            hybridKB = null;
        }
    }

}
