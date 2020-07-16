package edu.stanford.bmir.protege.web.client.nohrUIElements;



import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class ExtendedTextArea extends TextArea implements HasHandlers {

    private HandlerManager handlerManager;

    public ExtendedTextArea() {
        super();

        handlerManager = new HandlerManager(this);

        // For all browsers - catch onKeyUp
        sinkEvents(Event.ONKEYUP);

        // For IE and Firefox - catch onPaste
        sinkEvents(Event.ONPASTE);

        // For Opera - catch onInput
        /*sinkEvents(Event.ONINPUT);*/
    }

    @Override
    public void onBrowserEvent(Event event) {
        super.onBrowserEvent(event);

        switch (event.getTypeInt()) {
            case Event.ONKEYUP:
            case Event.ONPASTE:
            /*case Event.ONINPUT:*/
            {
                // Scheduler needed so pasted data shows up in TextBox before we fire event
                Scheduler.get().scheduleDeferred(() -> fireEvent(new NohrRuleChangeEvent()));
                break;
            }
            default:
                // Do nothing
        }
    }

    @Override
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    public HandlerRegistration addTextChangeEventHandler(NohrRuleChangeEventHandler handler) {
        return handlerManager.addHandler(NohrRuleChangeEvent.TYPE, handler);
    }
}
