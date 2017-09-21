package de.rwth.i2.attestor.main.phases.impl;

import de.rwth.i2.attestor.main.Attestor;
import de.rwth.i2.attestor.main.phases.AbstractPhase;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ParseGrammarPhase extends AbstractPhase {

    private boolean hasUserDefinedGrammar;

    @Override
    public String getName() {

        return "Parse grammar";
    }

    @Override
    protected void executePhase() {

        hasUserDefinedGrammar = settings.input().getGrammarLocation() != null;

        if(hasUserDefinedGrammar) {
            loadUserDefinedGrammar();
        } else {
            loadPredefinedGrammars();
        }
    }

    private void loadUserDefinedGrammar() {

        settings
                .grammar()
                .loadGrammarFromFile(
                        settings.input().getGrammarLocation(),
                        null
                );
    }

    private void loadPredefinedGrammars() {

        List<String> usedPredefinedGrammars = settings.input().getUsedPredefinedGrammars();

        if(usedPredefinedGrammars == null) {
            logger.warn( "No suitable predefined grammar could be found" );
            return;
        }

        for ( String predefinedGrammar : settings.input().getUsedPredefinedGrammars() ) {
            logger.debug("Loading predefined grammar " + predefinedGrammar);
            String locationOfRenamingMap = settings.input().getRenamingLocation( predefinedGrammar );
            try{
                HashMap<String,String> renamingMap = parseRenamingMap( locationOfRenamingMap );
                settings.grammar().loadGrammarFromURL(Attestor.class.getClassLoader()
                    .getResource("predefinedGrammars/" + predefinedGrammar + ".json"), renamingMap);
                }catch( FileNotFoundException e ){
                    logger.warn( "Skipping predefined grammar "
                            + predefinedGrammar + ".");
            }
        }
    }

    private HashMap<String, String> parseRenamingMap(String locationOfRenamingMap) throws FileNotFoundException {
        HashMap<String, String> rename = new HashMap<>();
        // Read in the type and field name mapping
        try {
            BufferedReader br = new BufferedReader(new java.io.FileReader( locationOfRenamingMap ));
            String definitionsLine = null;
            try {
                definitionsLine = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(definitionsLine != null){
                if(definitionsLine.startsWith("@Rename")){
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
            logger.warn("File " + locationOfRenamingMap + " not found. ");
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rename;
    }

    @Override
    public void logSummary() {
        // nothing to report
    }
}
