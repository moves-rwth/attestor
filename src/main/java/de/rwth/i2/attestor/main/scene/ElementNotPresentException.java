package de.rwth.i2.attestor.main.scene;

@SuppressWarnings("serial")
public class ElementNotPresentException extends Exception {

	public ElementNotPresentException() {
	}

	public ElementNotPresentException(String arg0) {
		super(arg0);
	}

	public ElementNotPresentException(Throwable arg0) {
		super(arg0);
	}

	public ElementNotPresentException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ElementNotPresentException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
