package edu.stanford.bmir.protege.web.shared.nohrquery;

import java.io.Serializable;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrQuery extends Serializable {

    String getQuery();

    void setQuery(String query);
}
