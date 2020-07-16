package edu.stanford.bmir.protege.web.client.nohrquery;

import java.util.LinkedList;
import java.util.List;

public class NohrQueriesProvider {

    List<NoHRQueryPresenter> views = new LinkedList<>();

    private static NohrQueriesProvider instance;

    public static NohrQueriesProvider getInstance() {
        if (instance == null)
            instance = new NohrQueriesProvider();
        return instance;
    }

    public void InsertView(NoHRQueryPresenter p) {
        if(!views.contains(p)) {
            views.add(p);
        }
    }

    public void removeView(NoHRQueryPresenter p) {
        if (views.contains(p)) {
            views.remove(p);
        }
    }

    public List<NoHRQueryPresenter> getViews() {
        return views;
    }
}
