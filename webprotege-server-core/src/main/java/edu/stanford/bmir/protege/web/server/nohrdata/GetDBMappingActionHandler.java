package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.GetDBMappingResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettings;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMappingImpl;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.OWLOntologyMerger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.model.*;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.GET_DBMAPPING;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.VIEW_NOHR;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class GetDBMappingActionHandler extends AbstractProjectActionHandler<GetDBMappingAction, GetDBMappingResult> {

    private static final Logger logger = LoggerFactory.getLogger(GetDBMappingActionHandler.class);
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
    public GetDBMappingActionHandler(@Nonnull AccessManager accessManager,
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
        return Arrays.asList(VIEW_NOHR, GET_DBMAPPING);
    }

    @Nonnull
    @Override
    public GetDBMappingResult execute(@Nonnull GetDBMappingAction action,
                                      @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();
        List<NohrDBMapping> mongoDBMappings = new LinkedList<>();
        List<NohrDBMapping> dbMappings = new LinkedList<>();
        List<NohrDBMapping> uiMappings = new LinkedList<>();
        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);

        try {
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

            List<NohrDatabaseSettings> dbSettings = repository.getDatabaseSettings(projectId);
            List<ODBCDriver> odbcDriversList = new LinkedList<>();

            for (NohrDatabaseSettings set : dbSettings) {
                DatabaseType databaseType = new DatabaseType(set.getDatabaseType());
                ODBCDriver odbcDriver = new ODBCDriverImpl(set.getConnectionName(), set.getUsername(), set.getPassword(), set.getDatabaseName(), databaseType);
                odbcDriversList.add(odbcDriver);
            }

            for (int i = 0; i < mongoDBMappings.size(); i++) {
                DBMapping tmp;
                try {
                    tmp = new DBMappingImpl(mongoDBMappings.get(i).getDbMapping(), odbcDriversList, i, parser.getVocabulary());
                    dbMappings.add(new NohrDBMappingImpl(tmp.getFileSyntax()));
                    uiMappings.add(new NohrDBMappingImpl(tmp.toString()));
                }
                catch (IOException e) {
                    logger.info("Failed to get dbMappings from database to {} on line {}", projectId, i);
                    System.out.println("Failed to get dbMappings from database to " + projectId + " on line "+i);
                }
            }

        } catch (Exception e) {
            logger.info("Failed to get dbMappings from database to {}, meaning project have no dbMappings added", projectId);
            System.out.println("Failed to get dbMappings from database to " + projectId + ", meaning project have no dbMappings added");
            return new GetDBMappingResult(projectId,
                    eventList,
                    null,null, NohrResponseCodes.DATABASE_ERROR);
        }

        return new GetDBMappingResult(projectId,
                eventList,
                dbMappings, uiMappings, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<GetDBMappingAction> getActionClass() {
        return GetDBMappingAction.class;
    }


}
