package edu.stanford.bmir.protege.web.client.nohrdatabasesettings;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import edu.stanford.bmir.protege.web.client.editor.ValueEditor;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.shared.DirtyChangedEvent;
import edu.stanford.bmir.protege.web.shared.DirtyChangedHandler;
import edu.stanford.bmir.protege.web.shared.nohrcodes.NohrDatabaseSettingsData;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 Jul 2018
 */
public class NohrDatabaseSettingsDataEditor extends Composite implements ValueEditor<NohrDatabaseSettingsData>, HasRequestFocus {

    @Nonnull
    private final NohrDatabaseSettingsDataView view;

    @Inject
    public NohrDatabaseSettingsDataEditor(@Nonnull NohrDatabaseSettingsDataView view) {
        this.view = view;
        initWidget(view.asWidget());
    }

    @Override
    public void requestFocus() {
        view.requestFocus();
    }

    @Override
    public void clearValue() {
        view.clear();
    }

    @Override
    public Optional<NohrDatabaseSettingsData> getValue() {
        /*Optional<OWLAnnotationPropertyData> property = view.getAnnotationProperty();
        String lang = view.getLang().toLowerCase();
        return property.map(prop -> DictionaryLanguageData.get(prop.getEntity().getIRI(), prop.getBrowserText(), lang));*/
        return null;
    }

    @Override
    public void setValue(NohrDatabaseSettingsData object) {
        /*object.getAnnotationPropertyData().ifPresent(view::setAnnotationProperty);
        view.setLang(object.getLanguageTag());*/
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Optional<NohrDatabaseSettingsData>> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public HandlerRegistration addDirtyChangedHandler(DirtyChangedHandler handler) {
        return addHandler(handler, DirtyChangedEvent.TYPE);
    }

    @Override
    public boolean isWellFormed() {
        return true;
    }
}
