package de.rwth.i2.attestor.grammar.canonicalization;

public class CannotMatchException extends Exception {

	public CannotMatchException() {
	}

	public CannotMatchException(String arg0) {
		super(arg0);
	}

	public CannotMatchException(Throwable arg0) {
		super(arg0);
	}

	public CannotMatchException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public CannotMatchException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
