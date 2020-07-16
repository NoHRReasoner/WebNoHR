package edu.stanford.bmir.protege.web.server.nohrdata;

import com.declarativa.interprolog.util.IPPrologError;
import com.google.common.base.Stopwatch;
import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.ExecNohrQueryAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.ExecNohrQueryResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrquery.NohrUIAnswer;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.InconsistentOntologyException;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.deductivedb.PrologEngineCreationException;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKB;
import pt.unl.fct.di.novalincs.nohr.hybridkb.NoHRHybridKBConfiguration;
import pt.unl.fct.di.novalincs.nohr.hybridkb.UnsupportedAxiomsException;
import pt.unl.fct.di.novalincs.nohr.model.*;
import pt.unl.fct.di.novalincs.nohr.plugin.NoHRInstance;
import pt.unl.fct.di.novalincs.nohr.translation.dl.DLInferenceEngine;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EXECUTE_QUERY;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.VIEW_NOHR;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class ExecNohrQueryActionHandler extends AbstractProjectActionHandler<ExecNohrQueryAction, ExecNohrQueryResult> {

    private static final Logger logger = LoggerFactory.getLogger(ExecNohrQueryActionHandler.class);

    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    private NohrRepository repository;

    private NohrQueryParser queryParser;

    private NohrRuleParser ruleParser;

    @Nonnull
    private final RevisionNumber revision;

    @Nonnull
    private final RevisionManager revisionManager;

    private OWLOntologyManager manager;

    private OWLOntology ontology;

    private UsersNohrInstances usersNoHRInstances;

    private Map<String, String> labelsName;

    @Nonnull
    private final Lock reentrantLock = new ReentrantLock();

    /*@Nonnull
    private DefaultOntologyIdManager defaultOntologyManager;*/

    @Inject
    public ExecNohrQueryActionHandler(@Nonnull AccessManager accessManager,
                                      @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull RevisionNumber revision,
                                      @Nonnull RevisionManager revisionManager, @Nonnull UsersNohrInstances usersNoHRInstances, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.usersNoHRInstances = usersNoHRInstances;
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.repository = checkNotNull(nohrRepository);
        /*repository.setChangedRules(projectId, true);*/
        this.labelsName = new HashMap<>();
        this.revision = checkNotNull(revision);
        this.revisionManager = checkNotNull(revisionManager);
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(VIEW_NOHR, EXECUTE_QUERY);
    }

    @Nonnull
    @Override
    public ExecNohrQueryResult execute(@Nonnull ExecNohrQueryAction action,
                                       @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
        String username = executionContext.getUserId().getUserName();

        reentrantLock.lock();
        if (usersNoHRInstances.isQueryExecuting(username, projectId)) {
            reentrantLock.unlock();
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.QUERYEXECUTING_ERROR);
        }
        usersNoHRInstances.setQueryExecuting(username, projectId, true);
        reentrantLock.unlock();

        NoHRHybridKB noHRHybridKB;
        List<NohrUIAnswer> gridUIAnswers = new LinkedList<>();
        Set<Rule> rules = new LinkedHashSet<>();
        List<String> variables = new LinkedList<>();
        List<Answer> answers;
        NohrWebReasonerInstance nohrWebReasonerInstance;
        NoHRInstance noHRInstance;
        NohrQueryParser nohrQueryParser;
        Query query;

        try {
            //Getting nohr instance or create a new one
            nohrWebReasonerInstance = usersNoHRInstances.getProjectNohrInstance(username, projectId);
            noHRInstance = nohrWebReasonerInstance.getNoHRInstance();

            //Instance is already started
            if (noHRInstance.isStarted()) {

                //Getting parser and labelsName for an ontology associated to a specific nohr instance
                labelsName = nohrWebReasonerInstance.getLabelsName();

                //Getting query parser for an ontology associated to a specific nohr instance
                nohrQueryParser = nohrWebReasonerInstance.getNohrQueryParser();

                reentrantLock.lock();
                query = nohrQueryParser.createQuery(action.getSourceText());
                reentrantLock.unlock();
                for (Variable variable : query.getVariables())
                    variables.add(variable.toString());

            } else {
                //Need to start NoHR Reasoner
                manager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
                ontology = manager.getOntologies().iterator().next();

                if (ontology != null) {
                    ruleParser = new NohrRuleParser(ontology);
                    queryParser = new NohrQueryParser(ontology);
                } else {
                    OWLOntologyMerger merger = new OWLOntologyMerger(manager);
                    try {
                        ontology = merger.createMergedOntology(manager, IRI.generateDocumentIRI());
                    } catch (OWLOntologyCreationException e) {
                        e.printStackTrace();
                    }
                    ruleParser = new NohrRuleParser(ontology);
                    queryParser = new NohrQueryParser(ontology);
                }

                reentrantLock.lock();
                query = queryParser.createQuery(action.getSourceText());
                reentrantLock.unlock();
                for (Variable variable : query.getVariables())
                    variables.add(variable.toString());

                //initialize a new labels map
                labelsName = new ConcurrentHashMap<>();

                //Create Rules through Nohr parser
                Stopwatch CreatingRulesTime = Stopwatch.createStarted();
                createRules(rules);
                logger.info("Creating nohr non monotonic rules to user {} for {} in {} ms", username, projectId, CreatingRulesTime.elapsed(MILLISECONDS));

                reentrantLock.lock();
                Program program = new HashSetProgram(rules);
                reentrantLock.unlock();

                NohrSettings nohrSettings = repository.getSettings(projectId);
                NoHRHybridKBConfiguration conf = new NoHRHybridKBConfiguration(nohrSettings.getELSetting(), nohrSettings.getQLSetting(), nohrSettings.getRLSetting(), nohrSettings.getDLEngineSetting().equals("HERMIT") ? DLInferenceEngine.HERMIT : DLInferenceEngine.KONCLUDE);

                reentrantLock.lock();
                DBMappingSet dbMappings = new HashSetDBMappingSet(new HashSet<DBMapping>());
                reentrantLock.unlock();
                List<NohrDatabaseSettings> dbSettings = repository.getDatabaseSettings(projectId);
                List<ODBCDriver> odbcDriversList = new LinkedList<>();

                for (NohrDatabaseSettings set : dbSettings) {
                    DatabaseType databaseType = new DatabaseType(set.getDatabaseType());
                    ODBCDriver odbcDriver = new ODBCDriverImpl(set.getConnectionName(), set.getUsername(), set.getPassword(), set.getDatabaseName(), databaseType);
                    odbcDriversList.add(odbcDriver);
                }

                List<NohrDBMapping> dbMappingsList = repository.getDBMappings(projectId);
                for (int i = 0; i < dbMappingsList.size(); i++) {
                    dbMappings.add(new DBMappingImpl(dbMappingsList.get(i).getDbMapping(), odbcDriversList, i, ruleParser.getVocabulary()));
                }

                Stopwatch stopwatch = Stopwatch.createStarted();
                noHRInstance.start(conf, queryParser.getOntology(), program, dbMappings, queryParser.getVocabulary());
                logger.info("Starting nohr to user {} for {} in {} ms", username, projectId, stopwatch.elapsed(MILLISECONDS));

                nohrWebReasonerInstance.setNoHRInstance(noHRInstance);
                nohrWebReasonerInstance.setNohrQueryParser(queryParser);
                nohrWebReasonerInstance.setLabelsName(getValuesFromLabels(ontology));

                usersNoHRInstances.setProjectNohrInstance(username, projectId, nohrWebReasonerInstance);
            }
            noHRHybridKB = noHRInstance.getHybridKB();

            Stopwatch stopwatch = Stopwatch.createStarted();
            answers = noHRHybridKB.allAnswers(query, action.getTrueAnswers(), action.getUndefinedAnswers(), action.getInconsistentAnswers());
            logger.info("Getting query answers for {} in {} ms", projectId, stopwatch.elapsed(MILLISECONDS));

            Stopwatch mappingTime = Stopwatch.createStarted();
            gridUIAnswers = mappingUiAnswers(answers);
            logger.info("Converting answers for UI format for {} in {} ms", projectId, mappingTime.elapsed(MILLISECONDS));

        } catch (OWLExpressionParserException e) {
            logger.info("Query is not valid in {}", projectId);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.PARSER_ERROR);
        } catch (UnsupportedAxiomsException e) {
            logger.info("Unsupported Axioms Exception in {}", projectId);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.UNSUPPORTEDAXIOMS_ERROR);
        } catch (PrologEngineCreationException e) {
            logger.info("Prolog Engine Creation Exception in {}", projectId);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.PROLOGENGINE_ERROR);
        } catch (InconsistentOntologyException e) {
            logger.info("Inconsistent Ontology Exception in {}", projectId);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.INCONSISTENTONTOLOGY_ERROR);
        } catch (IPPrologError e) {
            System.out.println(e);
            logger.info("prolog runtime error in {} ; Error :", projectId, e);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.UNKNOWN_ERROR);
        } catch (IOException e) {
            logger.info("IO error when trying to create database mappings in {}", projectId);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.UNKNOWN_ERROR);
        } catch (Exception e) {
            logger.info("Unknown error in {} ; Error: ", projectId, e);
            return new ExecNohrQueryResult(projectId,
                    eventList, null, null,
                    NohrResponseCodes.UNKNOWN_ERROR);
        } finally {
            usersNoHRInstances.setQueryExecuting(username, projectId, false);
        }

        if (!usersNoHRInstances.containsNohrInstance(username, projectId, nohrWebReasonerInstance))
            noHRInstance.stop();

        return new ExecNohrQueryResult(projectId,
                eventList, gridUIAnswers,
                variables, NohrResponseCodes.OK);
    }

    private List<NohrUIAnswer> mappingUiAnswers(List<Answer> answers) {

        List<NohrUIAnswer> gridUIAnswers = new LinkedList<>();

        for (Answer answer : answers) {
            List<Term> answersValues = answer.getValues();
            List<String> valuesList = new LinkedList<>();
            List<String> values = new LinkedList<>();
            for (int i = 0; i < answersValues.size(); i++) {
                String val;
                if (labelsName.containsKey(answersValues.get(i).toString())) {
                    values.add(labelsName.get(answersValues.get(i).toString()));
                    val = labelsName.get(answersValues.get(i).toString());
                } else {
                    values.add(answersValues.get(i).toString());
                    val = answersValues.get(i).toString();
                }
                valuesList.add(val);
            }
            gridUIAnswers.add(new NohrUIAnswer(answer.getValuation().toString().toLowerCase(), valuesList));
        }
        return gridUIAnswers;
    }

    private Map<String, String> getValuesFromLabels(OWLOntology o) {
        if (labelsName.isEmpty()) {
            for (OWLClass c : o.getClassesInSignature()) {
                for (OWLAnnotationAssertionAxiom a : o.getAnnotationAssertionAxioms(c.getIRI())) {
                    if (a.getProperty().isLabel() && a.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) a.getValue();
                        labelsName.putIfAbsent(c.toStringID(), val.getLiteral());
                    }
                }
            }

            for (OWLNamedIndividual i : o.getIndividualsInSignature()) {
                for (OWLAnnotationAssertionAxiom a : o.getAnnotationAssertionAxioms(i.getIRI())) {
                    if (a.getProperty().isLabel() && a.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) a.getValue();
                        labelsName.putIfAbsent(i.toStringID(), val.getLiteral());
                    }
                }
            }

            for (OWLObjectProperty p : o.getObjectPropertiesInSignature()) {
                for (OWLAnnotationAssertionAxiom a : o.getAnnotationAssertionAxioms(p.getIRI())) {
                    if (a.getProperty().isLabel() && a.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) a.getValue();
                        labelsName.putIfAbsent(p.toStringID(), val.getLiteral());
                    }
                }
            }

            for (OWLDataProperty d : o.getDataPropertiesInSignature()) {
                for (OWLAnnotationAssertionAxiom a : o.getAnnotationAssertionAxioms(d.getIRI())) {
                    if (a.getProperty().isLabel() && a.getValue() instanceof OWLLiteral) {
                        OWLLiteral val = (OWLLiteral) a.getValue();
                        labelsName.putIfAbsent(d.toStringID(), val.getLiteral());
                    }
                }
            }
        }
        return labelsName;
    }


    private void createRules(Set<Rule> rules) throws OWLExpressionParserException {

        /*boolean rulesChanged;
        if (rulesChanged = repository.getChangedRules(projectId) != null) {
            if (rulesChanged) {
                repository.setChangedRules(projectId, false);*/

        Collection<NohrRule> uiRules = repository.getRules(projectId);

        reentrantLock.lock();
        for (NohrRule rule : uiRules) {
            try {
                rules.add(ruleParser.createRule(rule.getRule()));
            } catch (OWLExpressionParserException e) {
                reentrantLock.unlock();
                throw new OWLExpressionParserException(e);
            }
        }
        reentrantLock.unlock();
    }
       /* }
    }*/

    @Nonnull
    @Override
    public Class<ExecNohrQueryAction> getActionClass() {
        return ExecNohrQueryAction.class;
    }
}
