package edu.stanford.bmir.protege.web.shared.nohrquery;



import java.io.Serializable;
import java.util.List;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrUIAnswer implements Serializable {

    private String truthValue;

    private List<String> values;

    public NohrUIAnswer(String truthValue, List<String> values) {
        this.truthValue = truthValue;
        this.values = values;
    }

    public NohrUIAnswer() {
    }

    public String getTruthValue() {
        return truthValue;
    }

    public List<String> getValues() {
        return values;
    }

    @Override
    public String toString() {
        return "NohrUIAnswer{" +
                "truthValue='" + truthValue + '\'' +
                ", values=" + values +
                '}';
    }
}
