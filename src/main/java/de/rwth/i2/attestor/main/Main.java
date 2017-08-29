package de.rwth.i2.attestor.main;


import java.util.concurrent.TimeUnit;

/**
 * Executes a program analysis with Attestor.
 *
 * @author Christoph
 */
class Main {

	public static void main( String[] args ) {

		// TODO This is for profiling with VisualVM only. Remove afterwards
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		Attestor main = new Attestor();
		main.run(args);
	}
}
