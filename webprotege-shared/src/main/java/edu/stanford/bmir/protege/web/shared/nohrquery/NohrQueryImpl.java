package edu.stanford.bmir.protege.web.shared.nohrquery;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrQueryImpl implements NohrQuery {

    private String query;

    public NohrQueryImpl(String query) {
        this.query = query;
    }

    public NohrQueryImpl() {
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String rule) {
        this.query = query;
    }

    @Override
    public String toString() { return query;}
}
