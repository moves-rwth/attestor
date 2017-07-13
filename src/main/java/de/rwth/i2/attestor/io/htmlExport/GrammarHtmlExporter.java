package de.rwth.i2.attestor.io.htmlExport;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.main.settings.Settings;

/**
 * A simple GrammarExporter that creates an HTML file for each grammar rule.
 * Each of these files is named according to the left-hand side of a rule and contains
 * an HTML representation of the HeapConfiguration on the right-hand side of the rule.
 */
public class GrammarHtmlExporter implements GrammarExporter {

    /**
     * The directory into which a file for each grammar rule will be written.
     */
	private final String directory;

    /**
     * Creates a new GrammarHtmlExporter.
     * @param directory The directory into which grammars should be exported.
     */
	public GrammarHtmlExporter( String directory ) {
		this.directory = directory;
	}

    /**
     * Creates an HTML file for each rule of the grammar.
     * @param grammar The grammar that should be exported.
     */
	public void export(Grammar grammar) {
        HeapConfigurationExporter exporter =
                Settings.getInstance().factory().getHeapConfigurationExporter(
                       directory
                );

		for( Nonterminal lhs : grammar.getAllLeftHandSides() ) {
			int index = 0;
			for( HeapConfiguration rhs : grammar.getRightHandSidesFor(lhs) ){
				exporter.export( computeFileName(lhs, index), rhs );
				index++;
			}
		}
	}

    /**
     * Computes the name of the file corresponding to the given rule.
     * @param nonterminal The left-hand side of the rule.
     * @param index The number of the rule.
     * @return The name of the file to be created for the given rule.
     */
	private String computeFileName(Nonterminal nonterminal, int index) {
	    return nonterminal + "_" + index;
    }

}
