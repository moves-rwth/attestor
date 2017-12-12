package de.rwth.i2.attestor.grammar.materialization.communication;

@SuppressWarnings("serial")
public class CannotMaterializeException extends Exception {


    public CannotMaterializeException() {

    }

    public CannotMaterializeException(String arg0) {

        super(arg0);
    }

    public CannotMaterializeException(Throwable arg0) {

        super(arg0);
    }

    public CannotMaterializeException(String arg0, Throwable arg1) {

        super(arg0, arg1);
    }

    public CannotMaterializeException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {

        super(arg0, arg1, arg2, arg3);
    }

}
