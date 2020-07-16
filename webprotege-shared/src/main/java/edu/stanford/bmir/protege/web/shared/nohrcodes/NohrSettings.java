package edu.stanford.bmir.protege.web.shared.nohrcodes;

import java.io.Serializable;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public interface NohrSettings extends Serializable {


    boolean getELSetting();

    boolean getQLSetting();

    boolean getRLSetting();

    String getDLEngineSetting();

    boolean equals(NohrSettings nohrSettings);
}
