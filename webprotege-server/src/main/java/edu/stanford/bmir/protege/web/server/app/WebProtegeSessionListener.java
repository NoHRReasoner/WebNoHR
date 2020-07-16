package edu.stanford.bmir.protege.web.server.app;

import edu.stanford.bmir.protege.web.server.nohrdata.UsersNohrInstances;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSession;
import edu.stanford.bmir.protege.web.server.session.WebProtegeSessionImpl;
import edu.stanford.bmir.protege.web.server.user.UserActivityManager;
import edu.stanford.bmir.protege.web.shared.inject.ApplicationSingleton;
import edu.stanford.bmir.protege.web.shared.user.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 12 Mar 2017
 */
@ApplicationSingleton
public class WebProtegeSessionListener implements HttpSessionListener {

    private final static Logger logger = LoggerFactory.getLogger(WebProtegeSessionListener.class);

    private final UserActivityManager userActivityManager;

    private UsersNohrInstances usersNohrInstances;

    @Inject
    public WebProtegeSessionListener(@Nonnull UserActivityManager userActivityManager, @Nonnull UsersNohrInstances usersNohrInstances) {
        this.userActivityManager = checkNotNull(userActivityManager);
        this.usersNohrInstances = usersNohrInstances;
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        WebProtegeSession session = new WebProtegeSessionImpl(httpSessionEvent.getSession());
        UserId userId = session.getUserInSession();
        //Checks if user is executing a query on any of is projects
        /*if (!usersNohrInstances.isQueryExecuting(userId.getUserName()))*/
        usersNohrInstances.stopProjectNohrInstance(userId.getUserName());
        if (!userId.isGuest()) {
            logger.info("{} Session expired", userId);
            userActivityManager.setLastLogout(userId, System.currentTimeMillis());
        }
    }
}
