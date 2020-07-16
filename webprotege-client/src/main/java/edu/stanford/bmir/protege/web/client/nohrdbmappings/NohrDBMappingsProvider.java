package edu.stanford.bmir.protege.web.client.nohrdbmappings;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class NohrDBMappingsProvider {

    List<NoHRDBMappingsPresenter> views = new LinkedList<>();
    Map<String, String> uiMapings = new HashMap<>();

    private static NohrDBMappingsProvider instance;

    public static NohrDBMappingsProvider getInstance() {
        if (instance == null)
            instance = new NohrDBMappingsProvider();
        return instance;
    }

    public String getDBFormatMapping(String uiFormat) {
        return uiMapings.get(uiFormat);
    }

    public void insertUiMapping(String uiMapping, String dbMapping) {
        uiMapings.putIfAbsent(uiMapping,dbMapping);
    }

    public void removeUiMapping(String uiMapping) {
        uiMapings.remove(uiMapping);
    }

    public void updateUiMapping(String oldKey, String newKey, String newValue) {
        uiMapings.remove(oldKey);
        uiMapings.putIfAbsent(newKey,newValue);
    }

    public void clearMappings() {
        uiMapings.clear();
    }

    public void insertView(NoHRDBMappingsPresenter p) {
        if(!views.contains(p)) {
            views.add(p);
        }
    }

    public void removeView(NoHRDBMappingsPresenter p) {
        if (views.contains(p)) {
            views.remove(p);
        }
    }

    public List<NoHRDBMappingsPresenter> getViews() {
        return views;
    }
}
