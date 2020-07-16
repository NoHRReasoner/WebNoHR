package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.change.HasApplyChanges;
import edu.stanford.bmir.protege.web.server.dispatch.AbstractProjectActionHandler;
import edu.stanford.bmir.protege.web.server.dispatch.ExecutionContext;
import edu.stanford.bmir.protege.web.server.entity.EntityNodeRenderer;
import edu.stanford.bmir.protege.web.server.events.EventManager;
import edu.stanford.bmir.protege.web.shared.access.BuiltInAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadRulesAction;
import edu.stanford.bmir.protege.web.shared.dispatch.actions.UploadRulesResult;
import edu.stanford.bmir.protege.web.shared.event.EventList;
import edu.stanford.bmir.protege.web.shared.event.EventTag;
import edu.stanford.bmir.protege.web.shared.event.ProjectEvent;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrResponseCodes;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRule;
import edu.stanford.bmir.protege.web.shared.nohrrules.NohrRuleImpl;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import static edu.stanford.bmir.protege.web.shared.access.BuiltInAction.UPLOAD_RULE;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UploadRulesActionHandler extends AbstractProjectActionHandler<UploadRulesAction, UploadRulesResult> {

    private static final Logger logger = LoggerFactory.getLogger(UploadRulesActionHandler.class);
    @Nonnull
    private final ProjectId projectId;

    private final EventManager<ProjectEvent<?>> eventManager;

    @Nonnull
    private final HasApplyChanges changeApplicator;

    @Nonnull
    private final EntityNodeRenderer renderer;

    private NohrRepository repository;

    private NohrRuleParser parser;

    private UsersNohrInstances usersNohrInstances;

    @Nonnull
    private final Lock reentrantLock = new ReentrantLock();

    private final String UPLOADS_PATH = "C:\\srv\\webprotege\\uploads\\";

    @Inject
    public UploadRulesActionHandler(@Nonnull AccessManager accessManager,
                                    @Nonnull ProjectId projectId, EventManager<ProjectEvent<?>> eventManager, @Nonnull HasApplyChanges changeApplicator,
                                    @Nonnull EntityNodeRenderer renderer, @Nonnull UsersNohrInstances usersNoHRInstances, @Nonnull NohrRepository nohrRepository) {
        super(accessManager);
        this.projectId = checkNotNull(projectId);
        this.eventManager = checkNotNull(eventManager);
        this.changeApplicator = checkNotNull(changeApplicator);
        this.renderer = checkNotNull(renderer);
        this.repository = checkNotNull(nohrRepository);
        this.parser = new NohrRuleParser();
        this.usersNohrInstances = usersNoHRInstances;
    }

    @Nonnull
    @Override
    protected Iterable<BuiltInAction> getRequiredExecutableBuiltInActions() {
        return Arrays.asList(EDIT_NOHR, UPLOAD_RULE);
    }

    @Nonnull
    @Override
    public UploadRulesResult execute(@Nonnull UploadRulesAction action,
                                     @Nonnull ExecutionContext executionContext) {
        EventTag eventTag = eventManager.getCurrentTag();

        EventList<ProjectEvent<?>> eventList = eventManager.getEventsFromTag(eventTag);
        List<NohrRule> rules = new LinkedList<>();
        File rulesFile = new File(UPLOADS_PATH + action.getFile());
        BufferedReader reader = null;
        int lineNumber = 1;

        try {
            logger.info("Reading temporary file to extract rules to database for {}", projectId);
            System.out.println("Reading temporary file to extract rules to database for " + projectId);
            reentrantLock.lock();
            reader = new BufferedReader(new FileReader(rulesFile));

            String strCurrentLine;
            while ((strCurrentLine = reader.readLine()) != null) {
                if(strCurrentLine.trim().length() > 0) {
                    parser.checkRule(strCurrentLine);
                    rules.add(new NohrRuleImpl(strCurrentLine));
                }
                lineNumber++;
            }
            reader.close();

            rules = rules.stream()
                    .distinct()
                    .collect(Collectors.toList());

            repository.insertRules(projectId, rules);
            usersNohrInstances.stopProjectNohrInstance(executionContext.getUserId().getUserName(), projectId);
        } catch (OWLExpressionParserException e) {
            logger.info("File contains rule(s) that are not valid in {}", projectId);
            System.out.println("File contains rule(s) that are not valid in " + projectId);
            return new UploadRulesResult(projectId,
                    eventList,
                    null, lineNumber, NohrResponseCodes.PARSER_ERROR);
        } catch (Exception e) {
            logger.info("Failed to insert rule(s) to database in {}", projectId);
            logger.info(e.toString());
            System.out.println("Failed to insert rule(s) to database in " + projectId);
            return new UploadRulesResult(projectId,
                    eventList,
                    null, lineNumber, NohrResponseCodes.DATABASE_ERROR);
        } finally {
            logger.info("Deleting temporary file {} in {}", rulesFile.getName(), projectId);
            System.out.println("Deleting temporary file " + rulesFile.getName() + " in " + projectId);
            reentrantLock.unlock();
            try {
                reader.close();
                rulesFile.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*try {
                Files.delete(Paths.get(UPLOADS_PATH+action.getFile()));
            }
            catch (IOException e) {
                logger.info("An error occurred when trying to delete temporary file");
            }*/
        }

        return new UploadRulesResult(projectId,
                eventList,
                rules, 0, NohrResponseCodes.OK);
    }

    @Nonnull
    @Override
    public Class<UploadRulesAction> getActionClass() {
        return UploadRulesAction.class;
    }
}
