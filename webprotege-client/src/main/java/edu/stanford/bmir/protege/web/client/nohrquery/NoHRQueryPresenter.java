package edu.stanford.bmir.protege.web.client.nohrquery;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import edu.stanford.bmir.protege.web.client.dispatch.DispatchServiceManager;
import edu.stanford.bmir.protege.web.client.permissions.LoggedInUserProjectPermissionChecker;
import edu.stanford.bmir.protege.web.client.selection.SelectionModel;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;
import edu.stanford.bmir.protege.web.shared.event.WebProtegeEventBus;
import edu.stanford.bmir.protege.web.shared.project.ProjectId;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NoHRQueryPresenter {

    private final DispatchServiceManager dsm;

    private final NoHRQueryView view;

    @Nonnull
    private final ProjectId projectId;

    private final SelectionModel selectionModel;

    private final LoggedInUserProjectPermissionChecker permissionChecker;

    private final NohrQueriesProvider nohrQueriesProvider;

    @Inject
    public NoHRQueryPresenter(NoHRQueryView view,
                              @Nonnull ProjectId projectId,
                              final SelectionModel selectionModel,
                              DispatchServiceManager dispatchServiceManager,
                              LoggedInUserProjectPermissionChecker permissionChecker,
                              NohrQueriesProvider nohrQueriesProvider) {
        this.projectId = projectId;
        this.selectionModel = selectionModel;
        this.permissionChecker = permissionChecker;
        this.view = view;
        this.dsm = dispatchServiceManager;
        this.nohrQueriesProvider = nohrQueriesProvider;
        this.view.addSelectionHandler(this::handleSelectionChangedInView);
    }

    public void start(AcceptsOneWidget container, WebProtegeEventBus eventBus) {
        GWT.log("[NoHRQueryPresenter] Started NoHR Query");
        container.setWidget(view.asWidget());
        //view.setInstanceRetrievalTypeChangedHandler(this::handleRetrievalTypeChanged);
    }

    private void handleSelectionChangedInView(SelectionEvent<List<EntityNode>> event) {
        event.getSelectedItem().stream().findFirst().ifPresent(sel -> selectionModel.setSelection(sel.getEntity()));
    }

    public NoHRQueryView getView(){
        return view;
    }

}
