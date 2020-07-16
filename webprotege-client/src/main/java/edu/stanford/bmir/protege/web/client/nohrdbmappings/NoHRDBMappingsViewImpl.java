package edu.stanford.bmir.protege.web.client.nohrdbmappings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchErrorMessageDisplay;
import edu.stanford.bmir.protege.web.client.list.ListBox;
import edu.stanford.bmir.protege.web.client.nohrpopupviews.UploadDBMappingPresenter;
import edu.stanford.bmir.protege.web.client.progress.BusyView;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NoHRDBMappingsViewImpl extends Composite implements NoHRDBMappingsView {

    private NohrDBMappingsListCellRenderer renderer;

    interface NoHRDBMappingsViewImplUiBinder extends UiBinder<HTMLPanel, NoHRDBMappingsViewImpl> {

    }

    private static NoHRDBMappingsViewImplUiBinder ourUiBinder = GWT.create(NoHRDBMappingsViewImplUiBinder.class);

    @Nonnull
    private final UploadDBMappingPresenter uploadDBMappingPresenter;

    @UiField
    protected ListBox<String, NohrDBMapping> dbMappingList;

    @UiField
    protected BusyView busyView;

    @Nonnull
    private final ProjectId projectId;

    private DispatchErrorMessageDisplay errorMessageDisplay;

    @Inject
    public NoHRDBMappingsViewImpl(@Nonnull NohrDBMappingsListCellRenderer renderer, @Nonnull UploadDBMappingPresenter uploadDBMappingPresenter, @Nonnull DispatchErrorMessageDisplay errorMessageDisplay, @Nonnull ProjectId projectId) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.uploadDBMappingPresenter = uploadDBMappingPresenter;
        this.errorMessageDisplay = errorMessageDisplay;
        this.renderer = checkNotNull(renderer);
        this.projectId = projectId;
        this.dbMappingList.setRenderer(renderer);
        this.dbMappingList.setKeyExtractor(dbMapping -> dbMapping.getDbMapping());
        /*this.downloadButton.setEnabled(true);
        this.uploadButton.setEnabled(true);*/
    }

    @Override
    public void setBusy(boolean busy) {
        GWT.log("Set Busy: " + busy);
        busyView.setVisible(busy);
    }

    @Override
    public void setDisplayLanguage(@Nonnull DisplayNameSettings language) {
        renderer.setDisplayLanguage(language);
        dbMappingList.setRenderer(renderer);
    }

    @Override
    public void updateNode(@Nonnull NohrDBMapping entityNode) {
        dbMappingList.updateElement(entityNode);
    }

    @Override
    public void setListData(List<NohrDBMapping> dbMappings) {
        dbMappingList.setListData(dbMappings);
    }

    @Override
    public List<NohrDBMapping> getListData() {
        return dbMappingList.getElements();
    }

    @Override
    public void addListData(NohrDBMapping dbMapping) {
        List<NohrDBMapping> elements = dbMappingList.getElements();
        elements.add(dbMapping);
        dbMappingList.setListData(elements);
    }

    @Override
    public void removeListData(Collection<NohrDBMapping> dbMappings) {
        List<NohrDBMapping> elements = dbMappingList.getElements();
        elements.removeAll(dbMappings);
        dbMappingList.setListData(elements);
    }

    @Override
    public void updateListData(NohrDBMapping newDBMapping, NohrDBMapping oldDBMapping) {
        List<NohrDBMapping> elements = dbMappingList.getElements();
        elements.set(elements.indexOf(oldDBMapping),newDBMapping);
        dbMappingList.setListData(elements);
    }

    @Override
    public Collection<NohrDBMapping> getSelectedDBMappings() {
        return dbMappingList.getSelection();
    }

    @Override
    public Optional<NohrDBMapping> getSelectedDBMapping() {
        return dbMappingList.getFirstSelectedElement();
    }

    @Override
    public void setSelectedDBMapping(NohrDBMapping dbMapping) {
        dbMappingList.setSelection(dbMapping.getDbMapping());
    }

    @Override
    public HandlerRegistration addSelectionHandler(SelectionHandler<List<NohrDBMapping>> handler) {
        return dbMappingList.addSelectionHandler(handler);
    }

    @Override
    public HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler) {
        return dbMappingList.addDoubleClickHandler(handler);
    }
}
