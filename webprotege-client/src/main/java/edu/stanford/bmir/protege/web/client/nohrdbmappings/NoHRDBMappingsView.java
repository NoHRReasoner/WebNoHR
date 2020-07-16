package edu.stanford.bmir.protege.web.client.nohrdbmappings;

import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.IsWidget;
import edu.stanford.bmir.protege.web.client.progress.HasBusy;
import edu.stanford.bmir.protege.web.shared.lang.DisplayNameSettings;
import edu.stanford.bmir.protege.web.shared.nohrdbmappings.NohrDBMapping;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */

public interface NoHRDBMappingsView extends HasSelectionHandlers<List<NohrDBMapping>>, HasBusy, IsWidget {

    void setDisplayLanguage(@Nonnull DisplayNameSettings language);

    void updateNode(@Nonnull NohrDBMapping entityNode);

    void setListData(List<NohrDBMapping> dbMappings);

    Collection<NohrDBMapping> getListData();

    void addListData(NohrDBMapping dbMapping);

    void updateListData(NohrDBMapping newNohrDBMapping, NohrDBMapping oldNohrDBMapping);

    void removeListData(Collection<NohrDBMapping> dbMappings);

    Collection<NohrDBMapping> getSelectedDBMappings();

    Optional<NohrDBMapping> getSelectedDBMapping();

    void setSelectedDBMapping(NohrDBMapping dbMapping);

    HandlerRegistration addDoubleClickHandler(DoubleClickHandler handler);
}
