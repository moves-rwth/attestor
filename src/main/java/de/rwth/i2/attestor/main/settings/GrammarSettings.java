package de.rwth.i2.attestor.main.settings;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.JsonToGrammar;
import de.rwth.i2.attestor.io.JsonToIndexedGrammar;
import de.rwth.i2.attestor.io.FileReader;

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
     * The grammar builder, that initially builds the (indexed) HRG.
     */
    private GrammarBuilder grammarBuilder = null;

    /**
     * Stores the renaming map, that is used for renaming of predefined grammars
     */
    HashMap<String,String> renamingMap;

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
            grammar = Grammar.builder().build();
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
     * Returns the renaming map read from the renaming file (for predefined grammars).
     * Note that null is returned, if grammars were not yet read or no predefined grammars
     * are used.
     * @return the renaming map in case predefined grammars are used and parsed already
     *      null otherwise
     */
    public HashMap<String, String> getRenamingMap() {
        return renamingMap;
    }

    public void setRenamingMap(HashMap<String, String> renamingMap) {
        this.renamingMap = renamingMap;
    }

    /**
     * Loads a graph grammar from a file and sets it as the graph grammar underlying the current analysis or extends
     * the previously loaded grammar (if present).
     *
     * @param filename The file storing the graph grammar.
     * @param rename defines the renaming of labels for this analysis
     */
    public void loadGrammarFromFile(String filename, HashMap<String, String> rename) {

        if(grammar != null)  {
            logger.debug("Extending previously set grammar.");
        }

        // The first time a grammar file is loaded
        if(grammarBuilder == null) {
            this.grammarBuilder = Grammar.builder();
        }

        try {
            String str = FileReader.read(filename);
            // Modify grammar (replace all keys in rename by its values)
            if(rename != null){
                for(HashMap.Entry<String, String> renaming : rename.entrySet()){
                    logger.debug("Renaming " + renaming.getKey() + " into " + renaming.getValue());
                    str = str.replaceAll("\"" + renaming.getKey() +"\"", "\"" + renaming.getValue() + "\"");
                }
            }

            logger.debug("Renamed grammar string: " + str);

            JSONArray array = new JSONArray(str);

            this.grammarBuilder.addRules(parseRules(array));


        } catch (FileNotFoundException e) {
            logger.error("Could not parse grammar at location " + filename + ". Skipping it.");
        }

        // Even if all grammar files could not be parsed, an empty grammar is created.
        this.grammar = grammarBuilder.build();

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

    public void loadGrammarFromURL(URL resource) {

        if(grammar != null)  {
            logger.debug("Extending previously set grammar.");
        }

        // The first time a grammar file is loaded
        if(grammarBuilder == null) {
            this.grammarBuilder = Grammar.builder();
        }

        try {
            InputStream is = resource.openStream();
            String str = FileReader.read(is);

            // Modify grammar (replace all keys in rename by its values)
            if(getRenamingMap() != null){
                for(HashMap.Entry<String, String> renaming : getRenamingMap().entrySet()){
                    logger.debug("Renaming " + renaming.getKey() + " into " + renaming.getValue());
                    str = str.replaceAll("\"" + renaming.getKey() +"\"", "\"" + renaming.getValue() + "\"");
                }
            }

            logger.debug("Renamed grammar string: " + str);

            JSONArray array = new JSONArray(str);

            this.grammarBuilder.addRules(parseRules(array));

        } catch (IOException e) {
            logger.error("Could not parse grammar at location " + resource.getPath() + ". Skipping it.");
        }

        // Even if all grammar files could not be parsed, an empty grammar is created.
        this.grammar = grammarBuilder.build();

    }
}
