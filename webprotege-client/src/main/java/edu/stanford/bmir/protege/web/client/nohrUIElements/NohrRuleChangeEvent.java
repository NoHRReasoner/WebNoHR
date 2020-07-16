package edu.stanford.bmir.protege.web.client.nohrUIElements;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class NohrRuleChangeEvent extends GwtEvent<NohrRuleChangeEventHandler> {


    public static final Type<NohrRuleChangeEventHandler> TYPE = new Type<NohrRuleChangeEventHandler>();

    @Override
    public Type<NohrRuleChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(NohrRuleChangeEventHandler handler) {
        handler.onTextChange(this);
    }
}
