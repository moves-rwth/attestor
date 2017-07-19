package de.rwth.i2.attestor.main.settings;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.materialization.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToGrammar;
import de.rwth.i2.attestor.io.JsonToIndexedGrammar;
import de.rwth.i2.attestor.util.FileReader;

/**
 *
 * Stores all settings related to the graph grammar underlying the current analysis.
 *
 * @author Christoph, Christina
 */
public class GrammarSettings {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger( "GrammarSettings" );

    /**
     * The graph grammar that is used by the currently loaded analysis.
     */
    private Grammar grammar = null;



    /**
     * Prevents creating objects of this class outside this package.
     */
    protected GrammarSettings() {
    }

    /**
     * @return The grammar underlying the currently loaded analysis.
     */
    public Grammar getGrammar() {

        if(grammar == null) {
            logger.warn("No grammar has been set. Proceeding with an empty grammar.");
            grammar = new Grammar(new HashMap<>());
        }

        return grammar;
    }

    /**
     * Sets the graph grammar used by the currently loaded analysis.
     * @param grammar The graph grammar that should be used.
     */
    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    /**
     * Loads a graph grammar from a file and sets it as the graph grammar underlying the current analysis.
     * @param filename The file storing the graph grammar.
     */
    public void loadGrammarFromFile(String filename, HashMap<String, String> rename) {

        if(grammar != null)  {
            logger.warn("Overwriting previously set grammar.");
        }

        try {
            String str = FileReader.read(filename);

            // Modify grammar (replace all keys in rename by its values)
            // TODO
            if(rename != null){

            }

            // TODO: adapt such that grammars are not overwritten but extended!
            JSONArray array = new JSONArray(str);
            GrammarBuilder grammarBuilder = Grammar.builder();
            grammarBuilder.addRules(parseRules(array));
            grammar = grammarBuilder.build();


        } catch (FileNotFoundException e) {
            logger.error("Could not parse grammar at location " + filename + ". Skipping it.");
        }

    }

    /**
     * Creates the rules of a graph grammar from a given JSONArray.
     * @param array A JSONArray that stores the grammar rules.
     * @return A mapping explicitly containing the rules by mapping rule's left-hand sides (nonterminals)
     *         to right-hand sides (heap configurations).
     */
    private Map<Nonterminal, Collection<HeapConfiguration>> parseRules(JSONArray array) {

        if(Settings.getInstance().options().isIndexedMode()) {
            return JsonToIndexedGrammar.parseForwardGrammar( array );
        } else {
            return JsonToGrammar.parseForwardGrammar( array );
        }
    }

    /**
     * Exports the grammar to a file.
     */
    public void exportGrammar() {

        if( Settings.getInstance().output().isExportGrammar() ) {

            String location = Settings.getInstance().output().getLocationForGrammar();
            
            //TODO
            //Settings.getInstance().factory().getGrammarExporter(location).export(grammar);
            logger.debug("Exported grammar to " + location );
        }
    }
}
