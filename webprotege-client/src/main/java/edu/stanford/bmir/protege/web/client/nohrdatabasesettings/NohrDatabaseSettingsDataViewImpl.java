package edu.stanford.bmir.protege.web.client.nohrdatabasesettings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import edu.stanford.bmir.protege.web.client.library.dlg.HasRequestFocus;
import edu.stanford.bmir.protege.web.client.primitive.DefaultLanguageEditor;
import edu.stanford.bmir.protege.web.client.primitive.LanguageEditor;
import edu.stanford.bmir.protege.web.client.primitive.PrimitiveDataEditorImpl;
import edu.stanford.bmir.protege.web.shared.entity.OWLAnnotationPropertyData;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 Jul 2018
 */
public class NohrDatabaseSettingsDataViewImpl extends Composite implements NohrDatabaseSettingsDataView, HasRequestFocus {

    interface NohrDatabaseSettingsDataViewImplUiBinder extends UiBinder<HTMLPanel, NohrDatabaseSettingsDataViewImpl> {

    }

    private static NohrDatabaseSettingsDataViewImplUiBinder ourUiBinder = GWT.create(NohrDatabaseSettingsDataViewImplUiBinder.class);

    @UiField(provided = true)
    PrimitiveDataEditorImpl annotationPropertyField;

    @UiField(provided = true)
    DefaultLanguageEditor languageField;

    @Inject
    public NohrDatabaseSettingsDataViewImpl(PrimitiveDataEditorImpl primitiveDataEditor,
                                            LanguageEditor languageEditor) {
        this.annotationPropertyField = primitiveDataEditor;
        this.languageField = (DefaultLanguageEditor) languageEditor;
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void requestFocus() {
        annotationPropertyField.requestFocus();
    }

    @Override
    public void clear() {
        annotationPropertyField.clearValue();
        languageField.clearValue();
    }

    @Override
    public void clearAnnotationProperty() {
        annotationPropertyField.clearValue();
    }

    @Override
    public void setAnnotationProperty(@Nonnull OWLAnnotationPropertyData annotationProperty) {
        annotationPropertyField.setValue(annotationProperty);
    }

    @Nonnull
    @Override
    public Optional<OWLAnnotationPropertyData> getAnnotationProperty() {
        return annotationPropertyField.getValue()
                                      .filter(val -> val instanceof OWLAnnotationPropertyData)
                                      .map(val -> ((OWLAnnotationPropertyData) val));
    }

    @Nonnull
    @Override
    public String getLang() {
        return languageField.getValue().orElse("");
    }

    @Override
    public void setLang(@Nonnull String lang) {
        languageField.setValue(lang);
    }
}