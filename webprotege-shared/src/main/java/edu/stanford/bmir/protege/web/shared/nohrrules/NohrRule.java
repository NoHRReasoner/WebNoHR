package edu.stanford.bmir.protege.web.shared.nohrrules;

import java.io.Serializable;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrRule extends Serializable {

    String getRule();

    void setRule(String rule);
}
