package de.rwth.i2.attestor.io.settings;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.main.phases.communication.InputSettings;
import de.rwth.i2.attestor.main.phases.communication.ModelCheckingSettings;
import de.rwth.i2.attestor.main.phases.communication.OutputSettings;
import de.rwth.i2.attestor.main.scene.Options;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

/**
 * @author Hannah Arndt, Christoph, Christina
 */
public class SettingsFileReader {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger("SettingsFileReader");

    /**
     * The JSONObject obtained from parsing the provided JSON file.
     */
    private JSONObject jsonSettings;

    /**
     * Creates a SettingsFileReader to parse a JSON file.
     *
     * @param file A JSON file that containsSubsumingState communication.
     */
    public SettingsFileReader(String file) {

        Scanner scan;
        try {

            scan = new Scanner(new FileReader(file));
            StringBuilder str = new StringBuilder();
            while (scan.hasNext())
                str.append(scan.nextLine()).append("\n");
            scan.close();

            jsonSettings = new JSONObject(str.toString());

        } catch (FileNotFoundException e) {
            logger.fatal(
                    "The communication file '" + file + "' could not be found. Execution is aborted.\n"
                            + "\tThe home directory of this tool is '" + System.getProperty("user.dir") + "'"
            );
            logger.fatal("Settings file '" + file + "' could not be found. Aborting.");
            System.exit(1);
        }
    }

    /**
     * Populates all input communication from the parsed communication file.
     *
     * @param input All input communication.
     * @return The populated input communication.
     */
    public void getInputSettings(InputSettings input) {

        JSONObject jsonInput = jsonSettings.getJSONObject("input");
        boolean hasDefaultPath = false;

        for (String key : jsonInput.keySet()) {

            switch (key) {
                case "defaultPath":
                    input.setDefaultPath(jsonInput.getString(key));
                    break;
                case "scenario":
                    input.setScenario(jsonSettings.getString(key));
                    break;
                case "program":
                    JSONObject programSettings = jsonInput.getJSONObject(key);
                    loadProgramSettings(input, programSettings);
                    break;
                case "userDefinedGrammar":
                    JSONObject grammarSettings = jsonInput.getJSONObject(key);
                    loadGrammarSettings(input, hasDefaultPath, grammarSettings);
                    break;
                case "predefinedGrammars":
                    JSONArray predefinedGrammarSettings = jsonInput.getJSONArray(key);
                    loadPredefinedGrammarSettings(input, predefinedGrammarSettings);
                    break;
                case "initialState":
                    JSONObject initialSettings = jsonInput.getJSONObject("initialState");
                    loadInitialStateSettings(input, hasDefaultPath, initialSettings);
                    break;
                case "contracts":
                    JSONObject contractSettings = jsonInput.getJSONObject("contracts");
                    loadContracts(contractSettings, input, hasDefaultPath);
                    break;
                default:
                    logger.error("Ignoring unknown option: " + key);
                    break;
            }
        }
    }

    private void loadContracts(JSONObject contractSettings, InputSettings input, boolean hasDefaultPath) {

        if (contractSettings.has("path")) {
            input.setPathToContracts(contractSettings.getString("path"));
        } else if (!hasDefaultPath && contractSettings.has("file")) {
            logger.error("You must define a default path or a path for the contracts");
        }
        JSONArray listOfFiles = contractSettings.getJSONArray("files");
        for (int i = 0; i < listOfFiles.length(); i++) {
            input.addContractFile(listOfFiles.getString(i));
        }
    }

    private void loadInitialStateSettings(InputSettings input, boolean hasDefaultPath, JSONObject initialSettings) {

        if (initialSettings.has("path")) {
            input.setPathToInput(initialSettings.getString("path"));
        } else if (!hasDefaultPath && initialSettings.has("file")) {
            logger.error("You must define a default path or a path for the initial state");
        }
        if (initialSettings.has("file")) {
            input.setInputName(initialSettings.getString("file"));
        } else if (input.getInputName() == null) {
            if (SettingsFileReader.class.getClassLoader().getResource("initialStates") == null) {
                throw new IllegalStateException("Default initial states location not found.");
            } else {
                input.setInitialStatesURL(SettingsFileReader.class.getClassLoader().getResource("initialStates/emptyInput.json"));
            }
        }
    }

    private void loadPredefinedGrammarSettings(InputSettings input, JSONArray predefinedGrammarSettings) {
        // Add requested predefined grammars
        for (int i = 0; i < predefinedGrammarSettings.length(); i++) {
            JSONObject predefinedGrammarSetting = predefinedGrammarSettings.getJSONObject(i);
            final String grammarType = predefinedGrammarSetting.getString("type");

            // Check if corresponding grammar exists
            if (SettingsFileReader.class.getClassLoader().getResource("predefinedGrammars/" + grammarType + ".json") != null) {

                String renameFileLocation = predefinedGrammarSetting.getString("definition");
                input.addPredefinedGrammar(grammarType, renameFileLocation);
                logger.debug("Adding predefined grammar " + grammarType);
            } else {
                logger.warn("No predefined grammar of type " + grammarType
                        + " available. Skipping it.");
            }
        }
    }

    private void loadGrammarSettings(InputSettings input, boolean hasDefaultPath, JSONObject grammarSettings) {

        if (grammarSettings.has("file")) {
            if (grammarSettings.has("path")) {
                input.setPathToGrammar(grammarSettings.getString("path"));
            } else if (!hasDefaultPath) {
                logger.error("You must define a default path or a path for the grammar");
            }
            input.setUserDefinedGrammarName(grammarSettings.getString("file"));
        }
    }

    private void loadProgramSettings(InputSettings input, JSONObject programSettings) {

        if (programSettings.has("classpath")) {
            input.setClasspath(programSettings.getString("classpath"));
        } else if (input.getClasspath() == null) {
            logger.error("You must define a default path or a classpath");
        }
        if (programSettings.has("class")) {
            input.setClassName(programSettings.getString("class"));
        } else {
            logger.error("Please provide a class to be analysed.");
        }
        if (programSettings.has("method")) {
            input.setMethodName(programSettings.getString("method"));
        } else {
            logger.error("Please provide a method to be analysed.");
        }
    }

    /**
     * Populates all option communication from the parsed communication file.
     *
     * @param options The options object to populate.
     */
    public void getOptionSettings(Options options) {

        JSONObject jsonOptions = jsonSettings.getJSONObject("options");

        for (String key : jsonOptions.keySet()) {
            switch (key) {
                case "mode":
                    options.setIndexedMode(jsonOptions.get(key).equals("indexed"));
                    break;
                case "abstractionDistance":
                    options.setAbstractionDistance(jsonOptions.getInt(key));
                    break;
                case "maximalStateSpace":
                    options.setMaxStateSpaceSize(jsonOptions.getInt(key));
                    break;
                case "maximalHeap":
                    options.setMaxStateSize(jsonOptions.getInt(key));
                    break;
                case "removeDeadVariables":
                    options.setRemoveDeadVariables(jsonOptions.getBoolean(key));
                    break;
                case "aggressiveNullAbstraction":
                    options.setAggressiveNullAbstraction(jsonOptions.getBoolean(key));
                    break;
                case "garbageCollection":
                    options.setGarbageCollectionEnabled(jsonOptions.getBoolean(key));
                    break;
                case "stateSpacePostProcessing":
                    options.setPostProcessingEnabled(jsonOptions.getBoolean(key));
                    break;
                default:
                    logger.error("Ignoring unknown option: " + key);
                    break;
            }
        }
    }

    /**
     * Populates all output communication from the parsed communication file.
     *
     * @param output All output communication.
     */
    public void getOutputSettings(OutputSettings output) {

        JSONObject jsonOutput = jsonSettings.getJSONObject("output");

        if (jsonOutput.has("defaultPath")) {
            output.setDefaultPath(jsonOutput.getString("defaultPath"));
        }

        if (jsonOutput.has("enabled")) {
            output.setNoExport(!jsonOutput.getBoolean("enabled"));
        }

        if (jsonOutput.has("stateSpace")) {
            output.setExportStateSpace(true);
            JSONObject jsonStateSpace = jsonOutput.getJSONObject("stateSpace");
            if (jsonStateSpace.has("path")) {
                output.setPathForStateSpace(jsonStateSpace.getString("path"));
            }
            if (jsonStateSpace.has("folder")) {
                output.setFolderForStateSpace(jsonStateSpace.getString("folder"));
            }
        }


        if (jsonOutput.has("grammar")) {
            output.setExportGrammar(true);
            JSONObject jsonGrammar = jsonOutput.getJSONObject("grammar");
            if (jsonGrammar.has("path")) {
                output.setPathForGrammar(jsonGrammar.getString("path"));
            }
            if (jsonGrammar.has("folder")) {
                output.setFolderForGrammar(jsonGrammar.getString("folder"));
            }
        }

        if (jsonOutput.has("customHCs")) {
            output.setExportCustomHcs(true);
            JSONObject jsonGrammar = jsonOutput.getJSONObject("customHCs");
            if (jsonGrammar.has("path")) {
                output.setPathForCustomHcs(jsonGrammar.getString("path"));
            }
            if (jsonGrammar.has("folder")) {
                output.setFolderForCustomHcs(jsonGrammar.getString("folder"));
            }
        }

        if (jsonOutput.has("contracts")) {
            output.setExportContracts(true);
            JSONObject jsonC = jsonOutput.getJSONObject("contracts");
            if (jsonC.has("path")) {
                output.setDirectoryForContracts(jsonC.getString("path"));
            }
            JSONArray requestArray = jsonC.getJSONArray("requestedContracts");
            for (int i = 0; i < requestArray.length(); i++) {
                JSONObject request = requestArray.getJSONObject(i);
                String signature = request.getString("signature");
                String filename = request.getString("filename");
                output.addRequiredContract(signature, filename);
            }
        }

        if( jsonOutput.has( "report" ) ){
            output.setExportReportOutput( true );
            JSONObject jsonReportOpt = jsonOutput.getJSONObject( "report" );
            if( jsonReportOpt.has("path" ) ){
                output.setFolderForReportOutput( jsonReportOpt.getString("path" ) );
            }
        }
    }

    /**
     * Populates the model checking communication with the input from the parsed communication file.
     *
     * @param mc all model checking communication
     */
    public void getMCSettings(ModelCheckingSettings mc) {

        JSONObject jsonMC = jsonSettings.getJSONObject("modelChecking");

        if (jsonMC.has("enabled")) {
            mc.setModelCheckingEnabled(jsonMC.getBoolean("enabled"));
        }
        if (jsonMC.has("formulae")) {
            String formulaeString = jsonMC.getString("formulae");
            for (String formula : formulaeString.split(";")) {
                try {
                    LTLFormula ltlFormula = new LTLFormula(formula);
                    // Transform to PNF
                    ltlFormula.toPNF();
                    mc.addFormula(ltlFormula);
                } catch (Exception e) {
                    logger.log(Level.WARN, "The input " + formula + " is not a valid LTL formula. Skipping it.");

                }
            }
        }
    }

}
