package edu.stanford.bmir.protege.web.server.download;

import com.google.common.util.concurrent.Striped;
import edu.stanford.bmir.protege.web.server.nohrdata.NohrRepository;
import edu.stanford.bmir.protege.web.server.project.ProjectDetailsManager;
import edu.stanford.bmir.protege.web.server.revision.HeadRevisionNumberFinder;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
@ApplicationSingleton
public class DBMappingDownloadService {

    private static final Logger logger = LoggerFactory.getLogger(DBMappingDownloadService.class);

    @Nonnull
    private final ExecutorService downloadGeneratorExecutor;

    @Nonnull
    private final ExecutorService fileTransferExecutor;

    @Nonnull
    private final ProjectDetailsManager projectDetailsManager;

    @Nonnull
    private final ProjectDownloadCache projectDownloadCache;

    @Nonnull
    private final HeadRevisionNumberFinder headRevisionNumberFinder;

    private final Striped<Lock> lockStripes = Striped.lazyWeakLock(10);

    @Nonnull
    private final CreateDownloadTaskFactory createDownloadTaskFactory;

    @Nonnull
    private final NohrRepository nohrRepository;

    @Nonnull
    private final Lock reentrantLock = new ReentrantLock();

    private final String UPLOADS_PATH = "C:\\srv\\webprotege\\uploads\\";

    @Inject
    public DBMappingDownloadService(@Nonnull @DownloadGeneratorExecutor ExecutorService downloadGeneratorExecutor,
                                    @Nonnull @FileTransferExecutor ExecutorService fileTransferExecutor,
                                    @Nonnull ProjectDetailsManager projectDetailsManager,
                                    @Nonnull ProjectDownloadCache projectDownloadCache,
                                    @Nonnull HeadRevisionNumberFinder headRevisionNumberFinder, @Nonnull CreateDownloadTaskFactory createDownloadTaskFactory, @Nonnull NohrRepository nohrRepository) {
        this.downloadGeneratorExecutor = checkNotNull(downloadGeneratorExecutor);
        this.fileTransferExecutor = checkNotNull(fileTransferExecutor);
        this.projectDetailsManager = checkNotNull(projectDetailsManager);
        this.projectDownloadCache = checkNotNull(projectDownloadCache);
        this.headRevisionNumberFinder = checkNotNull(headRevisionNumberFinder);
        this.createDownloadTaskFactory = checkNotNull(createDownloadTaskFactory);
        this.nohrRepository = checkNotNull(nohrRepository);
    }

    public void downloadDBMapping(@Nonnull UserId requester,
                                  @Nonnull ProjectId projectId,
                                  @Nonnull RevisionNumber revisionNumber,
                                  @Nonnull DownloadFormat downloadFormat,
                                  @Nonnull HttpServletResponse response) {

        String filename = "nohrDBMapping" + "_" + projectId.getId() + "_" + requester.getUserName() + ".tmp";
        List<NohrDBMapping> dbMappings = nohrRepository.getDBMappings(projectId);
        Path downloadPath = Paths.get(UPLOADS_PATH + filename);
        BufferedWriter writer = null;

        logger.info("Writing dbMappings to file in server in {}",projectId);
        System.out.println("Writing dbMappings to file in server in "+projectId);
        reentrantLock.lock();
        try {
            writer = new BufferedWriter(new FileWriter(UPLOADS_PATH + filename));
            Iterator<NohrDBMapping> iterator = dbMappings.iterator();
            while (iterator.hasNext()) {
                writer.write(iterator.next().getDbMapping() + "\n");
            }

            writer.close();

            transferFileToClient(projectId,
                    requester,
                    revisionNumber,
                    downloadFormat,
                    downloadPath,
                    response);
        } catch (IOException e) {
            logger.info("An error occurred when trying to write file to server in {}",projectId);
            System.out.println("An error occurred when trying to write file to server in "+projectId);
        } finally {
            logger.info("Deleting temporary file {} in {}", filename, projectId);
            System.out.println("Deleting temporary file " + filename + " in " + projectId);
            try {
                writer.close();
                downloadPath.toFile().delete();
                reentrantLock.unlock();
            } catch (IOException e) {
                reentrantLock.unlock();
                logger.info("An error occurred when trying to delete temporary file in {}",projectId);
                System.out.println("An error occurred when trying to delete temporary file in "+projectId);
            }
        }
    }

    private void transferFileToClient(@Nonnull ProjectId projectId,
                                      @Nonnull UserId userId,
                                      @Nonnull RevisionNumber revisionNumber,
                                      @Nonnull DownloadFormat downloadFormat,
                                      @Nonnull Path downloadSource,
                                      @Nonnull HttpServletResponse response) {

        String fileName = getClientSideFileName(projectId);
        FileTransferTask task = new FileTransferTask(projectId,
                userId,
                downloadSource,
                fileName,
                response);
        Future<?> transferFuture = fileTransferExecutor.submit(task);
        try {
            transferFuture.get();
        } catch (InterruptedException e) {
            logger.info("{} {} The download of dbMappings was interrupted.", projectId, userId);
        } catch (ExecutionException e) {
            logger.info("{} {} An execution exception occurred whilst transferring the project dbMappings.  Cause: {}",
                    projectId,
                    userId,
                    Optional.ofNullable(e.getCause()).map(Throwable::getMessage).orElse(""),
                    e.getCause());
        }
    }

    private String getClientSideFileName(ProjectId projectId) {
        /*uncomment to choose a file extension name for downloaded file,
        Note: it must match with the extension valid name for the upload action*/
        /*String fileName =
                "nohrDBMapping" +
                        "_" +
                        projectDetailsManager.getProjectDetails(projectId).getDisplayName()
                        + ".nohr";*/
        String fileName =
                "nohrDBMappings" +
                        "_" +
                        projectDetailsManager.getProjectDetails(projectId).getDisplayName();
        return fileName.toLowerCase();
    }

    /**
     * Shuts down this {@link DBMappingDownloadService}.
     */
    public void shutDown() {
        logger.info("Shutting down dbMappings Download Service");
        downloadGeneratorExecutor.shutdown();
        fileTransferExecutor.shutdown();
        logger.info("dbMappings Download Service has been shut down");
    }
}
