package edu.stanford.bmir.protege.web.shared.nohrrules;

import com.google.gwt.user.client.rpc.IsSerializable;
import edu.stanford.bmir.protege.web.shared.annotations.GwtSerializationConstructor;

import javax.annotation.Nonnull;

/**
 * Author: Tito Ferreira<br>
 * Fct NOVA<br>
 * Master degree in computer science<br>
 */
public class RuleAlreadyExistException extends RuntimeException implements IsSerializable {

    @GwtSerializationConstructor
    private RuleAlreadyExistException() {
    }

    public RuleAlreadyExistException(@Nonnull String message) {
        super(message);
    }
}
