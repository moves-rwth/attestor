package de.rwth.i2.attestor.grammar;

import java.io.IOException;

/**
 * A general method to export Grammars in a format to be specified by implementations.
 */
public interface GrammarExporter {

    /**
     * Exports the given grammar in an implementation specific format.
     * @param grammar The grammar that should be exported.
     */
    void export(Grammar grammar) throws IOException;

}
