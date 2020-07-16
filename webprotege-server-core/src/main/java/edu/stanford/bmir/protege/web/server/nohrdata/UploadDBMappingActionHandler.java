package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.server.revision.RevisionManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadDBMappingAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadDBMappingResult;
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
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.EDIT_NOHR;
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPLOAD_DBMAPPING;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UploadDBMappingActionHandler extends AbstractProjectActionHandler<UploadDBMappingAction, UploadDBMappingResult> {

    private static final Logger logger = LoggerFactory.getLogger(UploadDBMappingActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    private UsersNohrInstances usersNohrInstances;

    @Nonnull
    private final RevisionManager revisionManager;

    @Nonnull
    private final Lock reentrantLock = new ReentrantLock();

    private final String UPLOADS_PATH = "C:\\srv\\webprotege\\uploads\\";

    @Inject
    public UploadDBMappingActionHandler(@Nonnull AccessManager accessManager,
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
        this.usersNohrInstances = usersNoHRInstances;
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(EDIT_NOHR, UPLOAD_DBMAPPING);
    }

    @Nonnull
    @Override
    public UploadDBMappingResult execute(@Nonnull UploadDBMappingAction action,
                                         @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();

        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
        List<NohrDBMapping> dbMappings = new LinkedList<>();
        List<NohrDBMapping> uiMappings = new LinkedList<>();
        File dbMappingFile = new File(UPLOADS_PATH + action.getFile());
        BufferedReader reader = null;

        boolean duplicatedDBMapping;
        try {
            logger.info("Reading temporary file to extract dbMappings to database for {}", projectId);
            System.out.println("Reading temporary file to extract dbMappings to database for " + projectId);


            List<ODBCDriver> drivers = new LinkedList<>();
            List<NohrDatabaseSettings> databaseSettings = repository.getDatabaseSettings(projectId);
            for (NohrDatabaseSettings dbSettings : databaseSettings) {
                ODBCDriver tmp = new ODBCDriverImpl(
                        dbSettings.getConnectionName(),
                        dbSettings.getUsername(),
                        dbSettings.getPassword(),
                        dbSettings.getDatabaseName(),
                        new DatabaseType(dbSettings.getConnectionName()));

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

            reentrantLock.lock();
            reader = new BufferedReader(new FileReader(dbMappingFile));

            String strCurrentLine;
            int lineNumber = 1;
            while ((strCurrentLine = reader.readLine()) != null) {
                if (strCurrentLine.trim().length() > 0) {
                    DBMapping tmp;
                    try {
                        tmp = new DBMappingImpl(strCurrentLine, drivers, lineNumber, parser.getVocabulary());
                        dbMappings.add(new NohrDBMappingImpl(tmp.getFileSyntax()));
                        uiMappings.add(new NohrDBMappingImpl(tmp.toString()));
                        lineNumber++;
                    } catch (IOException e) {
                        logger.info("Failed to insert dbMappings in database on {} in line {}", projectId, lineNumber);
                        System.out.println("Failed to insert dbMappings in database on " + projectId + " in line " + lineNumber);

                        return new UploadDBMappingResult(projectId,
                                eventList,
                                dbMappings, uiMappings, lineNumber, NohrResponseCodes.PARSER_ERROR);
                    }
                }
            }
            reader.close();

            dbMappings = dbMappings.stream()
                    .distinct()
                    .collect(Collectors.toList());

            uiMappings = uiMappings.stream()
                    .distinct()
                    .collect(Collectors.toList());

            repository.insertDBMappings(projectId, dbMappings);
            usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(), projectId);
        } catch (Exception e) {
            logger.info("Failed to insert dbMapping(s) to database in {}; Error: {}", projectId, e.toString());
            System.out.println("Failed to insert dbMapping(s) to database in " + projectId + "; Error: " + e.toString());
            return new UploadDBMappingResult(projectId,
                    eventList,
                    null, null, 0, NohrResponseCodes.DATABASE_ERROR);
        } finally {
            logger.info("Deleting temporary file {} in {}", dbMappingFile.getName(), projectId);
            System.out.println("Deleting temporary file " + dbMappingFile.getName() + " in " + projectId);
            reentrantLock.unlock();
            try {
                reader.close();
                dbMappingFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return new UploadDBMappingResult(projectId,
                eventList,
                dbMappings, uiMappings, 0, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<UploadDBMappingAction> getActionClass() {
        return UploadDBMappingAction.class;
    }
}
