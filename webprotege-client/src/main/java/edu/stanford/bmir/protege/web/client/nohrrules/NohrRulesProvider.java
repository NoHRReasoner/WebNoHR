package edu.stanford.bmir.protege.web.client.nohrrules;

import java.util.LinkedList;
import java.util.List;

public class NohrRulesProvider {

    List<NoHRRulePresenter> views = new LinkedList<>();

    private static NohrRulesProvider instance;

    public static NohrRulesProvider getInstance() {
        if (instance == null)
            instance = new NohrRulesProvider();
        return instance;
    }

    public void InsertView(NoHRRulePresenter p) {
        if(!views.contains(p)) {
            views.add(p);
        }
    }

    public void removeView(NoHRRulePresenter p) {
        if (views.contains(p)) {
            views.remove(p);
        }
    }

    public List<NoHRRulePresenter> getViews() {
        return views;
    }
}
