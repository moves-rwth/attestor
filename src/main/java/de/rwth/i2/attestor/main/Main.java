package de.rwth.i2.attestor.main;


/**
 * Executes a program analysis with Attestor.
 *
 * @author Christoph
 */
class Main {

    public static void main(String[] args) {

        AbstractAttestor main = new Attestor();
        main.run(args);
    }
}
