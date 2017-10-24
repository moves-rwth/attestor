package de.rwth.i2.attestor.main;


/**
 * Executes a program analysis with Attestor.
 *
 * @author Christoph
 */
class Main {

	public static void main( String[] args ) {

		try {
			Thread.sleep(8000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Attestor main = new Attestor();
		main.run(args);
	}
}
