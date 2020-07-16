package edu.stanford.bmir.protege.web.shared.nohrrules;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrRuleImpl implements NohrRule {

    private String rule;

    public NohrRuleImpl(String rule) {
        this.rule = rule;
    }

    public NohrRuleImpl() {
    }

    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    @Override
    public String toString() { return rule;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NohrRuleImpl)) return false;

        NohrRuleImpl nohrRule = (NohrRuleImpl) o;

        return rule != null ? rule.equals(nohrRule.rule) : nohrRule.rule == null;
    }

    @Override
    public int hashCode() {
        return rule != null ? rule.hashCode() : 0;
    }
}
