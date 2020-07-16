package edu.stanford.bmir.protege.web.shared.nohrdbmappings;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrDBMappingImpl implements NohrDBMapping {

    private String dbMapping;

    public NohrDBMappingImpl(String dbMapping) {
        this.dbMapping = dbMapping;
    }

    public NohrDBMappingImpl() {
    }

    public String getDbMapping() {
        return dbMapping;
    }

    public void setDbMapping(String dbMapping) {
        this.dbMapping = dbMapping;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrDBMappingImpl)) return false;

        NohrDBMappingImpl that = (NohrDBMappingImpl) o;

        return dbMapping != null ? dbMapping.equals(that.dbMapping) : that.dbMapping == null;
    }

    @Override
    public int hashCode() {
        return dbMapping != null ? dbMapping.hashCode() : 0;
    }
}
