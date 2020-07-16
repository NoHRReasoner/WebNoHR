package edu.stanford.bmir.protege.web.client.projectsettings;

import java.util.LinkedList;
import java.util.List;

public class NohrDatabaseSectionsProvider {

    List<NohrDatabaseSettingsView> views = new LinkedList<>();

    private static NohrDatabaseSectionsProvider instance;

    public static NohrDatabaseSectionsProvider getInstance() {
        if (instance == null)
            instance = new NohrDatabaseSectionsProvider();
        return instance;
    }

    public void clearSections() {
        views.clear();
    }

    public void InsertView(NohrDatabaseSettingsView p) {
        if(!views.contains(p)) {
            views.add(p);
        }
    }

    public void removeView(NohrDatabaseSettingsView p) {
        if (views.contains(p)) {
            views.remove(p);
        }
    }

    public List<NohrDatabaseSettingsView> getViews() {
        return views;
    }
}
