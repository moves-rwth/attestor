package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values;

/**
 * Reports that a null pointer dereference has been detected by the analysis.
 *
 * @author Christoph
 */
public class NullPointerDereferenceException extends Throwable {

    private static final long serialVersionUID = 1L;

    /**
     * The value whose evaluation caused a null pointer dereference.
     */
    private final Value violation;

    public NullPointerDereferenceException(Value violation) {

        this.violation = violation;
    }

    public String getErrorMessage() {

        return "Null pointer dereference in " + violation;
    }

    public String getErrorMessage(Object append) {

        return "Null pointer dereference in " + violation + " - occurred in " + append;
    }

}
