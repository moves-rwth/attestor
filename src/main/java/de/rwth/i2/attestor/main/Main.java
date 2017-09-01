package de.rwth.i2.attestor.main;


/**
 * Executes a program analysis with Attestor.
 *
 * @author Christoph
 */
class Main {

	public static void main( String[] args ) {

		// TODO REMOVE, only used for VisualVM
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		long startTime = System.nanoTime();
		Attestor main = new Attestor();
		main.run(args);

		double elapsedTimeNs = (System.nanoTime() - startTime) / 1000000;
		System.out.println(elapsedTimeNs);
	}
}
