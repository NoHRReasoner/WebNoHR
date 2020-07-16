package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingInfoAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingInfoResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrColumnsTable;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrTablesTable;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.deductivedb.MappingGenerator;
import pt.unl.fct.di.novalincs.nohr.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.GET_DBMAPPING_INFO;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.VIEW_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class GetDBMappingInfoActionHandler extends AbstractProjectActionHandler<GetDBMappingInfoAction, GetDBMappingInfoResult> {

    private static final Logger logger = LoggerFactory.getLogger(GetDBMappingInfoActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    @Nonnull
    private final RevisionManager revisionManager;


    @Inject
    public GetDBMappingInfoActionHandler(@Nonnull AccessManager accessManager,
                                         @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull HasApplyChanges changeApplicator,
                                         @Nonnull EntityNodeRenderer renderer, @Nonnull RevisionManager revisionManager, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.repository = checkNotNull(nohrRepository);
        this.revisionManager = checkNotNull(revisionManager);
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(VIEW_NOHR, GET_DBMAPPING_INFO);
    }

    @Nonnull
    @Override
    public GetDBMappingInfoResult execute(@Nonnull GetDBMappingInfoAction action,
                                          @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        List<NohrDBMapping> mongoDBMappings = new LinkedList<>();
        List<NohrDBMapping> dbMappings = new LinkedList<>();
        List<NohrDBMapping> uiMappings = new LinkedList<>();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);

        String odbcDriver = "";
        List<NohrColumnsTable> selectColumnsList = new LinkedList<>();
        List<NohrTablesTable> fromTablesList = new LinkedList<>();
        String predicate = "";
        Integer arity = -1;
        String sql = "";

        try {

            List<NohrDatabaseSettings> dbSettings = repository.getDatabaseSettings(projectId);
            List<ODBCDriver> odbcDriversList = new LinkedList<>();

            for (NohrDatabaseSettings set : dbSettings) {
                DatabaseType databaseType = new DatabaseType(set.getDatabaseType());
                ODBCDriver driver = new ODBCDriverImpl(set.getConnectionName(), set.getUsername(), set.getPassword(), set.getDatabaseName(), databaseType);
                odbcDriversList.add(driver);
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

            mongoDBMappings = repository.getDBMappings(projectId);
            for (int i = 0; i < mongoDBMappings.size(); i++) {
                if (mongoDBMappings.get(i).getDbMapping().equals(action.getDbMapping())) {
                    DBMapping tmp = new DBMappingImpl(mongoDBMappings.get(i).getDbMapping(), odbcDriversList, i, parser.getVocabulary());
                    odbcDriver = tmp.getODBC().getConectionName();

                    //--------------------Columns-----------------------
                    List<String[]> cols = tmp.getColumns();
                    for (int j = 0; j < cols.size(); j++) {
                        String tableName = cols.get(j)[0].split(" as t")[0];
                        int tableNumber = Integer.parseInt(cols.get(j)[1].substring(1));
                        NohrColumnsTable uiCol = new NohrColumnsTable(tableName, cols.get(j)[2], cols.get(j)[3].equals("true") ? true : false);
                        uiCol.setNumber(tableNumber);
                        selectColumnsList.add(uiCol);
                    }

                    //--------------------Tables-----------------------
                    List<DBTable> tables = tmp.getTables();
                    for (int j = 0; j < tables.size(); j++) {
                        DBTable elem = tables.get(j);
                        List<String> newTableCol = new LinkedList<>();
                        for (String t : elem.getNewTableCol())
                            newTableCol.add(t);
                        List<String> oldTableCol = new LinkedList<>();
                        for (String c : elem.getOldTableCol())
                            oldTableCol.add(c);

                        String newTableColStr = "";
                        if (!newTableCol.isEmpty()) {
                            newTableColStr = newTableCol.get(0);

                        }
                        String oldTableColStr = "";
                        if (!oldTableCol.isEmpty())
                            oldTableColStr = oldTableCol.get(0);

                        NohrTablesTable uiTable = new NohrTablesTable(elem.getNewTableName(), newTableColStr, elem.getOldTableName(), oldTableColStr);

                        if (!elem.getNewTableAlias().isEmpty())
                            uiTable.setTableNumber(Integer.parseInt(elem.getNewTableAlias().substring(1)));

                        if (!elem.getOldTableAlias().isEmpty())
                            uiTable.setJoinNumber(Integer.parseInt(elem.getOldTableAlias().substring(1)));
                        fromTablesList.add(uiTable);
                    }
                    predicate = tmp.getPredicate().toString();
                    arity = tmp.getArity();
                    MappingGenerator sqlGenerator = new MappingGenerator(tmp, null);
                    sql = sqlGenerator.createSQL();
                    break;
                }
            }

        } catch (Exception e) {
            System.out.println(e);
            logger.info("Failed to get dbMappings from database to {}, meaning project have no dbMappings added", projectId);
            System.out.println("Failed to get dbMappings from database to " + projectId + ", meaning project have no dbMappings added");
            return new GetDBMappingInfoResult(projectId,
                    eventList,
                    null, new LinkedList<>(), new LinkedList<>(), null, null, null, NohrResponseCodes.DATABASE_ERROR);
        }

        return new GetDBMappingInfoResult(projectId,
                eventList, odbcDriver, selectColumnsList, fromTablesList, predicate, arity, sql, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<GetDBMappingInfoAction> getActionClass() {
        return GetDBMappingInfoAction.class;
    }


}
