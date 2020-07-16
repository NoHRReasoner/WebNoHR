package edu.stanford.bmir.protege.web.server.nohrdata;

import edu.stanford.bmir.protege.web.server.app.WebProtegeProperties;
import edu.stanford.bmir.protege.web.server.filemanager.ConfigDirectorySupplier;
import edu.stanford.bmir.protege.web.server.filemanager.ConfigInputStreamSupplier;
import edu.stanford.bmir.protege.web.server.inject.WebProtegePropertiesProvider;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.unl.fct.di.novalincs.nohr.plugin.NoHRInstance;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class UsersNohrInstances {

    private static final Logger logger = LoggerFactory.getLogger(UsersNohrInstances.class);

    private static UsersNohrInstances instance;

    //10 minutes
    /*private long timeout = 600*1000;*/
    private long timeout = getWebProtegeProperties().getNoHRInstanceTimer() * 60000;

    public UsersNohrInstances() throws IOException {
    }

    public static UsersNohrInstances getInstance() {
        if (instance == null) {
            try {
                instance = new UsersNohrInstances();
            } catch (IOException e) {
                System.out.println("Cannot create an instance of UsersNohrInstances Class");
                e.printStackTrace();
            }
        }
        return instance;
    }

    private Map<String, Map<String, NohrWebReasonerInstance>> nohrInstances = new ConcurrentHashMap<>();
    private Map<String, Map<String, Timer>> instancesTimer = new ConcurrentHashMap<>();

    private Map<String, Map<String, Boolean>> isQueryExecutingOnUserProject = new ConcurrentHashMap<>();
    private Map<String, Boolean> isQueryExecutingOnProject = new ConcurrentHashMap<>();
    /*private Map<String, Boolean> isQueryExecutingOnUser = new ConcurrentHashMap<>();*/

    /*public Map<String, Map<String, NohrWebReasonerInstance>> getMap() {
        return nohrInstances;
    }*/
    @Nonnull
    private static WebProtegeProperties getWebProtegeProperties() throws IOException {
        ConfigInputStreamSupplier configInputStreamSupplier = new ConfigInputStreamSupplier(new ConfigDirectorySupplier());
        WebProtegePropertiesProvider propertiesProvider = new WebProtegePropertiesProvider(configInputStreamSupplier);
        return propertiesProvider.get();
    }

    public void setQueryExecuting(@Nonnull String username, @Nonnull ProjectId pid, boolean flag) {
        String projectID = pid.getId();
        logger.info("Setting query executing for {}", pid);
        if (isQueryExecutingOnUserProject.containsKey(username)) {
            Map<String, Boolean> userQueryExecuting = isQueryExecutingOnUserProject.get(username);
            userQueryExecuting.put(projectID, flag);
        } else {
            Map<String, Boolean> tmp = new HashMap<>();
            tmp.put(projectID, flag);
            isQueryExecutingOnUserProject.putIfAbsent(username, tmp);
        }
        isQueryExecutingOnProject.put(projectID, flag);
    }

    public boolean isQueryExecuting(@Nonnull String username, @Nonnull ProjectId pid) {
        String projectID = pid.getId();
        logger.info("Checking if project is executing a nohr query for {}", pid);
        if (isQueryExecutingOnUserProject.containsKey(username)) {
            Map<String, Boolean> userQueryExecuting = isQueryExecutingOnUserProject.get(username);
            if (userQueryExecuting.containsKey(projectID))
                return userQueryExecuting.get(projectID);
            else
                return false;
        }
        return false;
    }

    public boolean isQueryExecuting(@Nonnull ProjectId pid) {
        String projectID = pid.getId();
        logger.info("Checking if project is executing a nohr query for {}", pid);

        if (isQueryExecutingOnProject.containsKey(projectID))
            return isQueryExecutingOnProject.get(projectID);

        return false;

    }

    /*public boolean isQueryExecuting(@Nonnull String username) {
        logger.info("Checking if user is executing a nohr query on any of is projects for user {}", username);
        if (isQueryExecutingOnUserProject.containsKey(username)) {
            for (Boolean val : isQueryExecutingOnUserProject.get(username).values()) {
                if (val)
                    return true;
            }
        }
        return false;
    }*/

    public boolean containsNohrInstance(String username, ProjectId pid, NohrWebReasonerInstance instance) {
        logger.info("Getting nohr ontology labels name for user {} in {}", username, pid);
        if (nohrInstances.containsKey(username))
            if (nohrInstances.get(username).containsValue(instance))
                return true;
        return false;
    }

    public void setProjectNohrInstance(String username, ProjectId pid, NohrWebReasonerInstance nohrWebReasonerInstance) {
        String projectID = pid.getId();
        Timer timer = new Timer();
        logger.info("Setting Nohr Web Reasoner Instance for user {} in {}", username, pid);

        insertTimer(username, pid, projectID, timer);
        if (nohrInstances.containsKey(username)) {
            Map<String, NohrWebReasonerInstance> userInstances = nohrInstances.get(username);
            userInstances.put(projectID, nohrWebReasonerInstance);
        }
    }

    public NohrWebReasonerInstance getProjectNohrInstance(String username, ProjectId pid) {
        String projectID = pid.getId();
        Timer timer = new Timer();

        logger.info("Getting nohr instance for user {} in {}", username, pid);

        if (nohrInstances.containsKey(username)) {
            Map<String, NohrWebReasonerInstance> userInstances = nohrInstances.get(username);
            if (userInstances.containsKey(projectID)) {
                NohrWebReasonerInstance nohrWebReasonerInstance = userInstances.get(projectID);

                //cancel the previous timer
                if (nohrWebReasonerInstance.getNoHRInstance().isStarted())
                    instancesTimer.get(username).get(projectID).cancel();

                //insert new timer
                insertTimer(username, pid, projectID, timer);
                return nohrWebReasonerInstance;
            } else {
                NoHRInstance instance = new NoHRInstance();
                NohrWebReasonerInstance nohrWebReasonerInstance = new NohrWebReasonerInstance(instance);
                userInstances.put(projectID, nohrWebReasonerInstance);
                return nohrWebReasonerInstance;
            }
        } else {
            NoHRInstance instance = new NoHRInstance();
            NohrWebReasonerInstance nohrWebReasonerInstance = new NohrWebReasonerInstance(instance);
            insertNohrInstance(username, projectID, nohrWebReasonerInstance);
            return nohrWebReasonerInstance;
        }
    }

    private void insertTimer(String username, ProjectId pid, String projectID, Timer timer) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                actionAfterTimeout(username, pid);
            }
        }, timeout);
        Map<String, Timer> projectTimer = new ConcurrentHashMap<>();
        projectTimer.put(projectID, timer);
        instancesTimer.put(username, projectTimer);
    }

    void actionAfterTimeout(String username, ProjectId project) {
        stopProjectNohrInstance(username, project);
        instancesTimer.get(username).remove(project);
        if (instancesTimer.get(username).size() == 0)
            instancesTimer.remove(username);
    }

    public void stopProjectNohrInstance(String username) {
        logger.info("Stopping all nohr instances for user {}", username);

        if (nohrInstances.containsKey(username)) {
            Map<String, NohrWebReasonerInstance> userInstances = nohrInstances.get(username);

            for (NohrWebReasonerInstance instance : userInstances.values()) {
                instance.getNoHRInstance().stop();
            }
            //check if its necessary
            for (String p : userInstances.keySet()) {
                removeQueryExecuting(username, p);
                removeTimer(username, p);
            }
            nohrInstances.remove(username);
            isQueryExecutingOnUserProject.remove(username);
            instancesTimer.remove(username);

        }
    }

    public void stopProjectNohrInstance(String username, ProjectId pid) {
        String projectID = pid.getId();
        logger.info("Stopping nohr instance for user {} in {}", username, pid);
        isQueryExecutingOnProject.remove(projectID);

        if (nohrInstances.containsKey(username)) {
            Map<String, NohrWebReasonerInstance> userInstances = nohrInstances.get(username);
            if (userInstances.containsKey(projectID)) {
                userInstances.get(projectID).getNoHRInstance().stop();
                userInstances.remove(projectID);

                removeQueryExecuting(username, projectID);
                removeTimer(username, projectID);
            }
        } else isQueryExecutingOnUserProject.remove(username);
    }


    public void stopProjectNohrInstance(ProjectId pid) {
        String projectID = pid.getId();
        logger.info("Stopping all nohr instances for project {}", pid);
        isQueryExecutingOnProject.remove(projectID);

        for (String user : nohrInstances.keySet()) {
            Map<String, NohrWebReasonerInstance> userProjects = nohrInstances.get(user);
            if (userProjects.containsKey(projectID)) {
                userProjects.get(projectID).getNoHRInstance().stop();
                userProjects.remove(projectID);

                removeQueryExecuting(user, projectID);
                removeTimer(user, projectID);
            }
        }
    }

    private void insertNohrInstance(String username, String projectID, NohrWebReasonerInstance instance) {
        Map<String, NohrWebReasonerInstance> userInstances = new ConcurrentHashMap<>();
        userInstances.put(projectID, instance);
        nohrInstances.put(username, userInstances);
    }

    private void removeQueryExecuting(String username, String projectID) {
        if (isQueryExecutingOnUserProject.containsKey(username))
            isQueryExecutingOnUserProject.get(username).remove(projectID);
    }

    private void removeTimer(String username, String projectID) {
        if (instancesTimer.containsKey(username) && instancesTimer.get(username).containsKey(projectID)) {
            instancesTimer.get(username).get(projectID).cancel();
            instancesTimer.get(username).remove(projectID);
        }
    }

    private void fixDataStructures(String username, String projectID) {
        if (isQueryExecutingOnUserProject.containsKey(username))
            isQueryExecutingOnUserProject.get(username).remove(projectID);
        if (instancesTimer.containsKey(username) && instancesTimer.get(username).containsKey(projectID)) {
            instancesTimer.get(username).get(projectID).cancel();
            instancesTimer.get(username).remove(projectID);
        }
    }
}
