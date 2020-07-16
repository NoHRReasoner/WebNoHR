package edu.stanford.bmir.protege.web.shared.nohrcodes;

public class NohrDatabaseSettingsImpl implements  NohrDatabaseSettings {

    private String connectionName;

    private String databaseName;

    private String databaseType;

    private String username;

    private String password;

    public NohrDatabaseSettingsImpl(String connectionName, String databaseName, String databaseType, String username, String password) {
        this.connectionName = connectionName;
        this.databaseName = databaseName;
        this.databaseType = databaseType;
        this.username = username;
        this.password = password;
    }

    public NohrDatabaseSettingsImpl() {
    }

    @Override
    public String getConnectionName() {
        return connectionName;
    }

    @Override
    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    @Override
    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public String getDatabaseType() {
        return databaseType;
    }

    @Override
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrDatabaseSettingsImpl)) return false;

        NohrDatabaseSettingsImpl that = (NohrDatabaseSettingsImpl) o;

        if (connectionName != null ? !connectionName.equals(that.connectionName) : that.connectionName != null)
            return false;
        if (databaseName != null ? !databaseName.equals(that.databaseName) : that.databaseName != null) return false;
        if (databaseType != null ? !databaseType.equals(that.databaseType) : that.databaseType != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        return password != null ? password.equals(that.password) : that.password == null;
    }

    @Override
    public int hashCode() {
        int result = connectionName != null ? connectionName.hashCode() : 0;
        result = 31 * result + (databaseName != null ? databaseName.hashCode() : 0);
        result = 31 * result + (databaseType != null ? databaseType.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NohrDatabaseSettingsImpl{" +
                "connectionName='" + connectionName + '\'' +
                ", databaseName='" + databaseName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
