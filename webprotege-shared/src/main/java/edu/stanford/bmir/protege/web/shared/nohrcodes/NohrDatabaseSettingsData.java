package edu.stanford.bmir.protege.web.shared.nohrcodes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import com.google.common.annotations.GwtCompatible;
import edu.stanford.bmir.protege.web.shared.shortform.DictionaryLanguage;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 17 Jul 2018
 */
@AutoValue
@GwtCompatible(serializable = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public abstract class NohrDatabaseSettingsData {

    private static final String DATABASE_NAME = "database name";

    private static final String DATABASE_TYPE = "database type";

    private static final String USERNAME = "username";

    private static final String PASSWORD = "password";

    private static final String PROPERTY_IRI = "propertyIri";

    private static final String LANGUAGE_TAG = "lang";

    private DictionaryLanguage dictionaryLanguage = null;


    @Nonnull
    public static NohrDatabaseSettingsData getData(@Nonnull String databaseName,
                                                   @Nonnull String databaseType,
                                                   @Nonnull String username,
                                                   @Nonnull String password) {
        return new AutoValue_NohrDatabaseSettingsData(databaseName, databaseType, username, password);
    }

    @JsonCreator
    @Nonnull
    public static NohrDatabaseSettingsData get(@Nonnull @JsonProperty(DATABASE_NAME) String databaseName,
                                               @Nonnull @JsonProperty(DATABASE_TYPE) String databaseType,
                                               @Nonnull @JsonProperty(USERNAME) String username,
                                               @Nonnull @JsonProperty(PASSWORD) String password) {
        return getData(databaseName, databaseType, username, password);
    }

    /*@JsonIgnore
    @Nonnull
    public NohrDatabaseSettings getDatabaseSettings() {
        return new NohrDatabaseSettingsImpl(getDatabaseName(), getDatabaseType(), getUsername(), getPassword());
    }*/

    @JsonProperty(DATABASE_NAME)
    @Nonnull
    public abstract String getDatabaseName();

    @JsonProperty(DATABASE_TYPE)
    @Nonnull
    public abstract String getDatabaseType();

    @JsonProperty(USERNAME)
    @Nonnull
    public abstract String getUsername();

    @JsonProperty(PASSWORD)
    @Nonnull
    public abstract String getPassword();

}
