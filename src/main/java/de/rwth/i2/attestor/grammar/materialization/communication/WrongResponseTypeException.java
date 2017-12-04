package de.rwth.i2.attestor.grammar.materialization.communication;

/**
 * This Exception is thrown if the GrammarResponse
 * cannot be handled.
 *
 * @author Hannah
 */
@SuppressWarnings("serial")
public class WrongResponseTypeException extends Exception {

    public WrongResponseTypeException() {

    }

    public WrongResponseTypeException(String message) {

        super(message);
    }

    public WrongResponseTypeException(Throwable cause) {

        super(cause);
    }

    public WrongResponseTypeException(String message, Throwable cause) {

        super(message, cause);
    }

    public WrongResponseTypeException(String message, Throwable cause, boolean enableSuppression,
                                      boolean writableStackTrace) {

        super(message, cause, enableSuppression, writableStackTrace);
    }

}
