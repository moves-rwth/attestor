package de.rwth.i2.attestor.phases.parser;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonToGrammar;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.Attestor;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;
import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

public class ParseGrammarPhase extends AbstractPhase implements GrammarTransformer {

    private GrammarBuilder grammarBuilder = null;
    private Grammar grammar;
    private InputSettings inputSettings;

    public ParseGrammarPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse grammar";
    }

    @Override
    public void executePhase() {

        inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        
        for( String grammarLocation : inputSettings.getUserDefinedGrammarFiles() ) {
            loadGrammarFromFile(grammarLocation);
        }

        loadPredefinedGrammars();
    }

    private void loadPredefinedGrammars() {

        for(String predefinedGrammar : inputSettings.getPredefinedGrammarNames()) {
            loadGrammarFromURL(Attestor.class.getClassLoader()
                    .getResource("predefinedGrammars/" + predefinedGrammar + ".json"));
        }
    }

    /**
     * Loads a graph grammar from a file and sets it as the graph grammar underlying the current analysis or extends
     * the previously loaded grammar (if present).
     *
     * @param filename The file storing the graph grammar.
     */
    public void loadGrammarFromFile(String filename) {

        if (grammar != null) {
            logger.debug("Extending previously set grammar.");
        }

        // The first time a grammar file is loaded
        if (grammarBuilder == null) {
            this.grammarBuilder = Grammar.builder();
        }

        try {
            String str = FileReader.read(filename);
            JSONArray array = new JSONArray(str);
            this.grammarBuilder.addRules(parseRules(array));
        } catch (FileNotFoundException e) {
            logger.error("Could not parse grammar at location " + filename + ". Skipping it.");
        }

        if(scene().options().isRuleCollapsingEnabled()) {
            grammarBuilder.updateCollapsedRules();
        }

        // Even if all grammar files could not be parsed, an empty grammar is created.
        this.grammar = grammarBuilder.build();
    }

    /**
     * Creates the rules of a graph grammar from a given JSONArray.
     *
     * @param array A JSONArray that stores the grammar rules.
     * @return A mapping explicitly containing the rules by mapping rule's left-hand sides (nonterminals)
     * to right-hand sides (heap configurations).
     */
    private Map<Nonterminal, Collection<HeapConfiguration>> parseRules(JSONArray array) {

        JsonToGrammar importer = new JsonToGrammar(this, inputSettings);
        return importer.parseForwardGrammar(array);
    }

    public void loadGrammarFromURL(URL resource) {

        if (grammar != null) {
            logger.debug("Extending previously set grammar.");
        }

        // The first time a grammar file is loaded
        if (grammarBuilder == null) {
            this.grammarBuilder = Grammar.builder();
        }

        try {
            InputStream is = resource.openStream();
            String str = FileReader.read(is);

            JSONArray array = new JSONArray(str);

            this.grammarBuilder.addRules(parseRules(array));

        } catch (IOException e) {
            logger.error("Could not parse grammar at location " + resource.getPath() + ". Skipping it.");
        }

        if(scene().options().isRuleCollapsingEnabled()) {
            grammarBuilder.updateCollapsedRules();
        }

        // Even if all grammar files could not be parsed, an empty grammar is created.
        this.grammar = grammarBuilder.build();

    }

    @Override
    public void logSummary() {
        // nothing to report
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }

    @Override
    public Grammar getGrammar() {

        return grammar;
    }

}
