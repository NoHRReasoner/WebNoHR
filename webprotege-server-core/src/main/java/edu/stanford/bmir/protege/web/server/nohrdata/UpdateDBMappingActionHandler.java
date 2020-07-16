package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateDBMappingAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UpdateDBMappingResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMappingImpl;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.model.*;
import pt.unl.fct.di.novalincs.nohr.utils.CreatingMappings;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_NOHR;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPDATE_DBMAPPING;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UpdateDBMappingActionHandler extends AbstractProjectActionHandler<UpdateDBMappingAction, UpdateDBMappingResult> {

    private static final Logger logger = LoggerFactory.getLogger(UpdateDBMappingActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    /*private pt.unl.fct.di.novalincs.nohr.plugin.test.NohrRuleParser parser;*/

    private UsersNohrInstances usersNohrInstances;

    @Nonnull
    private final RevisionManager revisionManager;

    @Inject
    public UpdateDBMappingActionHandler(@Nonnull AccessManager accessManager,
                                        @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager,
                                        @Nonnull HasApplyChanges changeApplicator,
                                        @Nonnull EntityNodeRenderer renderer,
                                        @Nonnull RevisionManager revisionManager,
                                        @Nonnull UsersNohrInstances usersNoHRInstances,
                                        @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.revisionManager = revisionManager;
        this.repository = checkNotNull(nohrRepository);
        /*this.parser = new pt.unl.fct.di.novalincs.nohr.plugin.test.NohrRuleParser();*/
        this.usersNohrInstances = usersNoHRInstances;
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(EDIT_NOHR, UPDATE_DBMAPPING);
    }

    @Nonnull
    @Override
    public UpdateDBMappingResult execute(@Nonnull UpdateDBMappingAction action,
                                         @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        /*NohrDBMapping newDBMapping = new NohrDBMappingImpl(action.getNewDBMapping());*/
        /*NohrDBMapping oldDBMapping = new NohrDBMappingImpl(action.getOldDBMapping());*/
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);

        DatabaseType databaseType = new DatabaseType(action.getOdbcDriver());

        ODBCDriver odbcDriver = null;
        List<ODBCDriver> drivers = new LinkedList<>();
        List<NohrDatabaseSettings> databaseSettings = repository.getDatabaseSettings(projectId);
        for (NohrDatabaseSettings dbSettings : databaseSettings) {
            ODBCDriver tmp = new ODBCDriverImpl(
                    dbSettings.getConnectionName(),
                    dbSettings.getUsername(),
                    dbSettings.getPassword(),
                    dbSettings.getDatabaseName(),
                    databaseType);

            if (dbSettings.getConnectionName().equals(action.getOdbcDriver())) {
                odbcDriver = tmp;
            }

            drivers.add(tmp);
        }

        OWLOntologyManager manager = revisionManager.getOntologyManagerForRevision(revisionManager.getCurrentRevision());
        OWLOntology ontology = manager.getOntologies().iterator().next();
        NohrRuleParser parser;

        if (ontology != null) {
            parser = new NohrRuleParser(ontology);
        } else {
            OWLOntologyMerger merger = new OWLOntologyMerger(manager);
            try {
                ontology = merger.createMergedOntology(manager, IRI.generateDocumentIRI());
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
            parser = new NohrRuleParser(ontology);
        }

        DBMapping getNewDBMapping;
        DBMapping getOldDBMapping;


        try {
            //User add a database mapping using the grids
            if (action.getFromTablesList().length > 0) {
                //------------------Tables--------------------------
                List<DBTable> dbTables = new LinkedList<>();
                NohrTablesTable[] uiTables = action.getFromTablesList();
                for (int i = 0; i < uiTables.length; i++) {
                    NohrTablesTable elem = uiTables[i];
                    List<String> newTableCol = new LinkedList<>();
                    newTableCol.add(elem.getColumn());
                    List<String> oldTableCol = new LinkedList<>();
                    oldTableCol.add(elem.getOnColumn());
                    DBTable tmp = new DBTable(elem.getTable(), elem.getJoinTable(), elem.getTableNumber(), elem.getTableJoinNumber(), newTableCol, oldTableCol, uiTables[0].equals(elem));
                    dbTables.add(tmp);
                }

                //------------------Columns--------------------------
                List<String[]> dbColumns = new LinkedList<>();
                NohrColumnsTable[] uiColumns = action.getSelectColumnsList();
                for (int i = 0; i < uiColumns.length; i++) {
                    NohrColumnsTable elem = uiColumns[i];
                    String[] strArray = new String[4];
                    strArray[0] = elem.getTableWithNumber();
                    strArray[1] = elem.getTableNumber();
                    strArray[2] = elem.getColumnCol();
                    strArray[3] = elem.getFloatingCol() ? "true" : "false";
                    dbColumns.add(strArray);
                }

                final List<Term> kbTerms = new LinkedList<>();
                for (int i = 0; i < action.getArity(); i++) {
                    Term kbTerm = Model.var(CreatingMappings.getVar(action.getArity(), i));
                    kbTerms.add(kbTerm);
                }
                Rule tmp = Model.rule(Model.atom(parser.getVocabulary(), action.getPredicate(), kbTerms));
                Predicate predicate = tmp.getHead().getFunctor();

                int aliasNumber = repository.getDBMappings(projectId).size();

                getNewDBMapping = new DBMappingImpl(odbcDriver, dbTables, dbColumns, predicate, aliasNumber + 1);
            }
            //User add a database mapping writing sql
            else {

                final List<Term> kbTerms = new LinkedList<>();
                for (int i = 0; i < action.getArity(); i++) {
                    Term kbTerm = Model.var(CreatingMappings.getVar(action.getArity(), i));
                    kbTerms.add(kbTerm);
                }
                Rule tmp = Model.rule(Model.atom(parser.getVocabulary(), action.getPredicate(), kbTerms));
                Predicate predicate = tmp.getHead().getFunctor();

                getNewDBMapping = new DBMappingImpl(odbcDriver, action.getSql(), action.getArity(), predicate);
            }

            getOldDBMapping = new DBMappingImpl(action.getOldDBMapping(), drivers, 1, parser.getVocabulary());

        } catch (Exception e) {
            logger.info("Failed to create getNewDBMapping in {}", projectId);
            System.out.println("Failed to create getNewDBMapping in " + projectId);
            return new UpdateDBMappingResult(projectId,
                    eventList, null, null,
                    null, null, NohrResponseCodes.UNKNOWN_ERROR);
        }

        NohrDBMapping newDBMapping = new NohrDBMappingImpl(getNewDBMapping.getFileSyntax());
        NohrDBMapping newUIMapping = new NohrDBMappingImpl(getNewDBMapping.toString());

        NohrDBMapping oldDBMapping = new NohrDBMappingImpl(getOldDBMapping.getFileSyntax());
        NohrDBMapping oldUIMapping = new NohrDBMappingImpl(getOldDBMapping.toString());


        try {
            repository.updateDBMapping(projectId, oldDBMapping.getDbMapping(), newDBMapping.getDbMapping());
            usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(),projectId);
        }  catch (Exception e) {
            logger.info("Failed to update database Mapping {} to {} database in {}",oldUIMapping.getDbMapping(), newUIMapping.getDbMapping(), projectId);
            System.out.println("Failed to update database Mapping "+oldDBMapping.getDbMapping()+" to database in "+projectId);
            return new UpdateDBMappingResult(projectId,
                    eventList, null, null,
                    null,null, NohrResponseCodes.DATABASE_ERROR);
        }

        System.out.println("Old database mapping");
        System.out.println(oldDBMapping.getDbMapping());
        System.out.println("New database mapping");
        System.out.println(newDBMapping.getDbMapping());
        System.out.println("Old UI mapping");
        System.out.println(newUIMapping.getDbMapping());
        System.out.println("New UI mapping");
        System.out.println(newUIMapping.getDbMapping());
        return new UpdateDBMappingResult(projectId,
                eventList,
                oldDBMapping, newDBMapping, oldUIMapping, newUIMapping, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<UpdateDBMappingAction> getActionClass() {
        return UpdateDBMappingAction.class;
    }
}
