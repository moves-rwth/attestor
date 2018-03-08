package de.rwth.i2.attestor.grammar;


import java.io.IOException;

/**
 * A general method to export Grammars in a format to be specified by implementations.
 */
public interface GrammarExporter {

    /**
     * Exports the given grammar in an implementation specific format.
     *
     * @param directory The path to where the grammar data should be exported
     * @param grammar   The grammar that should be exported.
     * @throws IOException if writing of exported files fails.
     */
    void export(String directory, Grammar grammar) throws IOException;
}
