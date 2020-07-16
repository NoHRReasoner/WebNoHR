package edu.stanford.bmir.protege.web.client.nohrquery;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.shared.entity.EntityNode;

import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */

public interface NoHRQueryView extends HasSelectionHandlers<List<EntityNode>>, HasBusy, IsWidget {

    Button getExecQueryButton();

    CheckBox getTrueCheckBox();

    CheckBox getInconsistentCheckBox();

    CheckBox getUndefinedCheckBox();
}
