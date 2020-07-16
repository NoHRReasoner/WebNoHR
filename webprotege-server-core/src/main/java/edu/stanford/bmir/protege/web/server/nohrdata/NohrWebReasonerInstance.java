package edu.stanford.bmir.protege.web.server.nohrdata;

import pt.unl.fct.di.novalincs.nohr.plugin.NoHRInstance;

import java.util.Map;

public class NohrWebReasonerInstance {
    
    private NoHRInstance noHRInstance;

    private NohrQueryParser nohrQueryParser;

    private Map<String,String> labelsName;

    public NohrWebReasonerInstance() {
    }

    public NohrWebReasonerInstance(NoHRInstance noHRInstance) {
        this.noHRInstance = noHRInstance;
    }

    public NohrWebReasonerInstance(NoHRInstance noHRInstance, NohrQueryParser nohrQueryParser, Map<String, String> labelsName) {
        this.noHRInstance = noHRInstance;
        this.nohrQueryParser = nohrQueryParser;
        this.labelsName = labelsName;
    }

    public NoHRInstance getNoHRInstance() {
        return noHRInstance;
    }

    public void setNoHRInstance(NoHRInstance noHRInstance) {
        this.noHRInstance = noHRInstance;
    }

    public NohrQueryParser getNohrQueryParser() {
        return nohrQueryParser;
    }

    public void setNohrQueryParser(NohrQueryParser nohrQueryParser) {
        this.nohrQueryParser = nohrQueryParser;
    }

    public Map<String, String> getLabelsName() {
        return labelsName;
    }

    public void setLabelsName(Map<String, String> labelsName) {
        this.labelsName = labelsName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrWebReasonerInstance)) return false;

        NohrWebReasonerInstance that = (NohrWebReasonerInstance) o;

        if (noHRInstance != null ? !noHRInstance.equals(that.noHRInstance) : that.noHRInstance != null) return false;
        if (nohrQueryParser != null ? !nohrQueryParser.equals(that.nohrQueryParser) : that.nohrQueryParser != null)
            return false;
        return labelsName != null ? labelsName.equals(that.labelsName) : that.labelsName == null;
    }

    @Override
    public int hashCode() {
        int result = noHRInstance != null ? noHRInstance.hashCode() : 0;
        result = 31 * result + (nohrQueryParser != null ? nohrQueryParser.hashCode() : 0);
        result = 31 * result + (labelsName != null ? labelsName.hashCode() : 0);
        return result;
    }
}
