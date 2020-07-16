package edu.stanford.bmir.protege.web.shared.nohrquery;

import java.io.Serializable;
import java.util.List;

public class AnswersTable implements Serializable {

    private String truthValueCol;

    private List<String> values;

    public AnswersTable() {
    }

    public AnswersTable(String truthValueCol, List<String> values) {
        this.truthValueCol = truthValueCol;
        this.values = values;
    }

    public String getTruthValueCol() {
        return truthValueCol;
    }

    public void setTruthValueCol(String truthValueCol) {
        this.truthValueCol = truthValueCol;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "AnswersTable{" +
                "truthValueCol='" + truthValueCol + '\'' +
                ", variable='" + values + '\'' +
                '}';
    }
}
