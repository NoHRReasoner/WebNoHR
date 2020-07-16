package edu.stanford.bmir.protege.web.shared.nohrcodes;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrSettingsImpl implements NohrSettings {

    private boolean dlInferenceEngineEL;
    private boolean dlInferenceEngineQL;
    private boolean dlInferenceEngineRL;
    private String dlInferenceEngine;

    public NohrSettingsImpl() {
    }

    public NohrSettingsImpl(boolean dlInferenceEngineEL, boolean dlInferenceEngineQL, boolean dlInferenceEngineRL, String dlInferenceEngine) {
        this.dlInferenceEngineEL = dlInferenceEngineEL;
        this.dlInferenceEngineQL = dlInferenceEngineQL;
        this.dlInferenceEngineRL = dlInferenceEngineRL;
        this.dlInferenceEngine = dlInferenceEngine;
    }

    public boolean getELSetting() {
        return dlInferenceEngineEL;
    }

    public boolean getQLSetting() {
        return dlInferenceEngineQL;
    }

    public boolean getRLSetting() {
        return dlInferenceEngineRL;
    }

    public String getDLEngineSetting() {
        return dlInferenceEngine;
    }

    public void setELSetting(boolean elSetting) {
        dlInferenceEngineEL = elSetting;
    }

    public void setQLSetting(boolean qlSetting) {
        dlInferenceEngineQL = qlSetting;
    }

    public void setRLSetting(boolean rlSetting) {
        dlInferenceEngineRL = rlSetting;
    }

    public void setDLEngineSetting(String engineSetting) {
        dlInferenceEngine = engineSetting;
    }

    @Override
    public boolean equals(NohrSettings nohrSettings) {
        if (this == nohrSettings)
            return true;

        if (this.dlInferenceEngineEL != nohrSettings.getELSetting())
            return false;

        if (this.dlInferenceEngineQL != nohrSettings.getQLSetting())
            return false;

        if (this.dlInferenceEngineRL != nohrSettings.getRLSetting())
            return false;

        if (!this.dlInferenceEngine.equals(nohrSettings.getDLEngineSetting()))
            return false;

        return true;
    }

    @Override
    public String toString() {
        return "EL:" + dlInferenceEngineEL + " ; QL:" + dlInferenceEngineQL + " ; RL:" + dlInferenceEngineRL + " ; Engine: " + dlInferenceEngine;
    }
}
