package de.rwth.i2.attestor.phases.parser;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.json.JSONArray;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonToGrammar;
import de.rwth.i2.attestor.io.jsonImport.JsonToIndexedGrammar;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.Attestor;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.communication.InputSettings;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.InputSettingsTransformer;

public class ParseGrammarPhase extends AbstractPhase implements GrammarTransformer {

    private GrammarBuilder grammarBuilder = null;
    private Grammar grammar;
    private Map<String, String> renamingMap = null;

    public ParseGrammarPhase(Scene scene) {

        super(scene);
    }

    @Override
    public String getName() {

        return "Parse grammar";
    }

    @Override
    public void executePhase() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        
        if ( inputSettings.hasUserDefinedGrammar() ) {
        	for( String grammarLocation : inputSettings.getGrammarLocations() )
        		loadGrammarFromFile(grammarLocation);
        }

        boolean hasPredefinedGrammars = inputSettings.getUsedPredefinedGrammars() != null;
        if (hasPredefinedGrammars) {
            loadPredefinedGrammars();
        }
    }

    private void loadPredefinedGrammars() {

        InputSettings inputSettings = getPhase(InputSettingsTransformer.class).getInputSettings();
        List<String> usedPredefinedGrammars = inputSettings.getUsedPredefinedGrammars();

        if (usedPredefinedGrammars == null) {
            logger.error("No suitable predefined grammar could be found");
            return;
        }

        for (String predefinedGrammar : inputSettings.getUsedPredefinedGrammars()) {
            logger.debug("Loading predefined grammar " + predefinedGrammar);
            String locationOfRenamingMap = inputSettings.getRenamingLocation(predefinedGrammar);
            try {
                renamingMap = parseRenamingMap(locationOfRenamingMap);
                loadGrammarFromURL(Attestor.class.getClassLoader()
                        .getResource("predefinedGrammars/" + predefinedGrammar + ".json"));
            } catch (FileNotFoundException e) {
                logger.error("Skipping predefined grammar "
                        + predefinedGrammar + ".");
            }
        }
    }

    private HashMap<String, String> parseRenamingMap(String locationOfRenamingMap) throws FileNotFoundException {

        HashMap<String, String> rename = new LinkedHashMap<>();
        // Read in the type and field name mapping
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader(locationOfRenamingMap));
            String definitionsLine = null;
            try {
                definitionsLine = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (definitionsLine != null) {
                if (definitionsLine.startsWith("@Rename")) {
                    String[] map = definitionsLine.replace("@Rename", "").split("->");
                    assert map.length == 2;

                    rename.put(map[0].trim(), map[1].trim());
                }

                try {
                    definitionsLine = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            br.close();
        } catch (FileNotFoundException e) {
            logger.error("File " + locationOfRenamingMap + " not found. ");
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rename;
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
            // Modify grammar (replace all keys in rename by its values)
            if (renamingMap != null) {
                for (HashMap.Entry<String, String> renaming : renamingMap.entrySet()) {
                    logger.debug("Renaming " + renaming.getKey() + " into " + renaming.getValue());
                    str = str.replaceAll("\"" + renaming.getKey() + "\"", "\"" + renaming.getValue() + "\"");
                }
            }

            logger.debug("Renamed grammar string: " + str);

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

        if (scene().options().isIndexedMode()) {
            JsonToIndexedGrammar importer = new JsonToIndexedGrammar(this);
            return importer.parseForwardGrammar(array);
        } else {
            JsonToGrammar importer = new JsonToGrammar(this);
            return importer.parseForwardGrammar(array);
        }
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

            // Modify grammar (replace all keys in rename by its values)
            if (getRenamingMap() != null) {
                for (HashMap.Entry<String, String> renaming : getRenamingMap().entrySet()) {
                    logger.debug("Renaming " + renaming.getKey() + " into " + renaming.getValue());
                    str = str.replaceAll("\"" + renaming.getKey() + "\"", "\"" + renaming.getValue() + "\"");
                }
            }

            logger.debug("Renamed grammar string: " + str);

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

    @Override
    public Map<String, String> getRenamingMap() {

        return renamingMap;
    }
}
