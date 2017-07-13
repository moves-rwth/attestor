package de.rwth.i2.attestor.util;

/**
 * An exception that is thrown if a graph transformation tries to access selectors of a heap configuration that
 * are hidden inside of a nonterminal hyperedge.
 *
 * @author Hannah Arndt
 */
public class NotSufficientlyMaterializedException extends Exception {

	private static final long serialVersionUID = -1056090328610939401L;

	public NotSufficientlyMaterializedException() {
		super();
	}

	public NotSufficientlyMaterializedException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotSufficientlyMaterializedException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotSufficientlyMaterializedException(String message) {
		super(message);
	}

	public NotSufficientlyMaterializedException(Throwable cause) {
		super(cause);
	}

	
}
