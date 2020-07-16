package edu.stanford.bmir.protege.web.shared.nohrdbmappings;

import java.io.Serializable;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrDBMapping extends Serializable {

    String getDbMapping();

    void setDbMapping(String dbMapping);
}
