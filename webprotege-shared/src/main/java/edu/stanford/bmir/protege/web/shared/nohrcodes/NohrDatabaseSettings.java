package edu.stanford.bmir.protege.web.shared.nohrcodes;

import java.io.Serializable;

public interface NohrDatabaseSettings extends Serializable {

    String getConnectionName();

    void setConnectionName(String connectionName);

    String getDatabaseName();

    void setDatabaseName(String databaseName);

    String getDatabaseType();

    void setDatabaseType(String databaseType);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);
}
