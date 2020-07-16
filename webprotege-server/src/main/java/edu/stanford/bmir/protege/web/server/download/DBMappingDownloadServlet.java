package edu.stanford.bmir.protege.web.server.download;

import edu.stanford.bmir.protege.web.server.access.AccessManager;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import edu.stanford.bmir.protege.web.shared.revision.RevisionNumber;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
@ApplicationSingleton
public class DBMappingDownloadServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(DBMappingDownloadServlet.class);

    @Nonnull
    private final AccessManager accessManager;

    @Nonnull
    private final DBMappingDownloadService dbMappingDownloadService;


    @Inject
    public DBMappingDownloadServlet(@Nonnull AccessManager accessManager,
                                    @Nonnull DBMappingDownloadService dbMappingDownloadService) {
        this.accessManager = accessManager;
        this.dbMappingDownloadService = dbMappingDownloadService;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final WebProtegeSession webProtegeSession = new WebProtegeSessionImpl(req.getSession());
        UserId userId = webProtegeSession.getUserInSession();
        FileDownloadParameters downloadParameters = new FileDownloadParameters(req);

        startDBMappingDownload(resp, userId, downloadParameters);

    }

    private void startDBMappingDownload(HttpServletResponse resp,
                                        UserId userId,
                                        FileDownloadParameters downloadParameters) throws IOException {
        ProjectId projectId = downloadParameters.getProjectId();
        RevisionNumber revisionNumber = downloadParameters.getRequestedRevision();
        DownloadFormat format = downloadParameters.getFormat();
        dbMappingDownloadService.downloadDBMapping(userId, projectId, revisionNumber, format, resp);
    }

    @Override
    public void destroy() {
        super.destroy();
        dbMappingDownloadService.shutDown();
    }
}
