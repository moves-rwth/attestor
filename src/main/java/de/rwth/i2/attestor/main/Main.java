package de.rwth.i2.attestor.main;

import de.rwth.i2.attestor.main.settings.Settings;

/**
 * Executes a program analysis with Attestor.
 *
 * @author Christoph
 */
class Main {

	public static void main( String[] args ) {
		
		Attestor main = new Attestor();
		main.run(args);
	}
}
