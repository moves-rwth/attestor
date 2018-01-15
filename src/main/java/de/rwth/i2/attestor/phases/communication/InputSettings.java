package de.rwth.i2.attestor.phases.communication;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * All global communication regarding input files.
 *
 * @author Christoph, Christina
 */
public class InputSettings {

    /**
     * The paths to the file specifying the renaming used for the
     * predefined grammars
     */
    private final HashMap<String, String> pathsToGrammar2RenameDefinition = new LinkedHashMap<>();
    /**
     * The url to the default empty initial state.
     */
    public URL initialStatesURL;
    /**
     * filenames containing user defined contracts to use
     */
    ArrayList<String> contractFiles = new ArrayList<>();
    /**
     * Short name of the analyzed scenario (optional).
     */
    private String name = "";
    /**
     * Description of the analyzed scenario (optional).
     */
    private String scenario = "";

    /**
     * The path to the settings file
     */
    private String pathToSettingsFile = "";
    /**
     * The classpath of source code files that are analyzed.
     */
    private String classpath;
    /**
     * The class that is analyzed.
     */
    private String className;
    /**
     * The initial method that is analyzed.
     */
    private String methodName;
    /**
     * The path to the file specifying the graph grammar underlying the analysis.
     */
    private String pathToGrammar;
    /**
     * The name of the file of the graph grammar underlying the analysis.
     */
    private String userDefinedGrammarName = null;
    /**
     * The list of predefined grammars used by the current analysis
     */
    private ArrayList<String> usedPredefinedGrammars;
    /**
     * The mapping from predefined grammars to their rename mapping
     */
    private HashMap<String, HashMap<String, String>> grammar2RenameMap;
    /**
     * path to the files storing user defined contracts
     */
    private String pathToContracts;
    /**
     * The path to the file specifying the initial state.
     */
    private String pathToInput;
    /**
     * The name of the file specifying the initial state.
     */
    private String inputName;

    public URL getInitialStatesURL() {

        return initialStatesURL;
    }

    public void setInitialStatesURL(URL resource) {

        this.initialStatesURL = resource;
    }

    public String getInputName() {

        return inputName;
    }

    /**
     * Sets the name of the file holding the initial state.
     *
     * @param inputName The name of the file holding the initial state.
     */
    public void setInputName(String inputName) {

        this.inputName = inputName;
    }

    /**
     * Sets the default path to search for all possible input files.
     *
     * @param path The default path.
     */
    public void setDefaultPath(String path) {

        classpath = path;
        pathToGrammar = path;
        pathToInput = path;
    }

    public String getName() {

        return name;
    }

    public String getScenario() {

        return scenario;
    }

    public void setName(String name) {

        this.name = name;
    }

    public void setScenario(String scenario) {

        this.scenario = scenario;
    }

    /**
     * @return The path to the classes that are analyzed.
     */
    public String getClasspath() {

        return classpath;
    }

    /**
     * Sets the path to the classes that are analyzed.
     *
     * @param classpath The path to the classes that are analyzed.
     */
    public void setClasspath(String classpath) {

        this.classpath = classpath;
    }

    /**
     * @return The name of the class that is analyzed.
     */
    public String getClassName() {

        return className;
    }

    /**
     * Sets the name of the class that should be analyzed.
     *
     * @param className The classes' name.
     */
    public void setClassName(String className) {

        this.className = className;
    }

    /**
     * @return The name of the method that should be analyzed.
     */
    public String getMethodName() {

        return methodName;
    }

    /**
     * Sets the name of the method that should be analyzed.
     *
     * @param methodName The name of the method to analyze.
     */
    public void setMethodName(String methodName) {

        this.methodName = methodName;
    }

    /**
     * Sets the path to the file containing the graph grammar underlying the analysis.
     *
     * @param pathToGrammar The path to the file containing the graph grammar.
     */
    public void setPathToGrammar(String pathToGrammar) {

        this.pathToGrammar = pathToGrammar;
    }

    /**
     * Returns the name of the file containing the user-defined graph grammar underlying the analysis.
     *
     * @return the location of the userDefinedGrammar
     */
    public String getUserDefinedGrammarName() {

        return this.userDefinedGrammarName;
    }

    /**
     * Sets the name of the file containing the user-defined graph grammar underlying the analysis.
     *
     * @param userDefinedGrammarName The name of the file containing the graph grammar.
     */
    public void setUserDefinedGrammarName(String userDefinedGrammarName) {

        this.userDefinedGrammarName = userDefinedGrammarName;
    }

    /**
     * Returns the path to the files containing user defined contracts
     *
     * @return the location of the user defined contracts
     */
    public String getPathToContracts() {

        return this.pathToContracts;
    }

    /**
     * Sets the path to the files containing user defined contracts
     */
    public void setPathToContracts(String pathToContracts) {

        this.pathToContracts = pathToContracts;
    }

    /**
     * Adds the provided name to the list of contract files to consider for this run
     *
     * @param name the filename
     */
    public void addContractFile(String name) {

        this.contractFiles.add(name);
    }

    /**
     * Returns the filenames of the user defined contracts to use
     *
     * @return a list containing the filenames
     */
    public ArrayList<String> getContractFileNames() {

        return this.contractFiles;
    }

    /**
     * @return The fully qualified path to the file holding the graph grammar underlying the analysis.
     */
    public String getGrammarLocation() {

        return pathToGrammar + File.separator + userDefinedGrammarName;
    }

    /**
     * Sets the path to the file holding the initial state.
     *
     * @param pathToInput The directory that containsSubsumingState the file holding the initial state.
     */
    public void setPathToInput(String pathToInput) {

        this.pathToInput = pathToInput;
    }

    /**
     * @return The fully qualified path to the file holding the initial state.
     */
    public String getInputLocation() {

        return pathToInput + File.separator + inputName;
    }

    public void setRootPath(String rootPath) {

        this.classpath = rootPath + File.separator + this.classpath;
        this.pathToGrammar = rootPath + File.separator + this.pathToGrammar;
        this.pathToInput = rootPath + File.separator + this.pathToInput;
        this.pathToContracts = rootPath + File.separator + this.pathToContracts;
        for (java.util.Map.Entry<String, String> entry : pathsToGrammar2RenameDefinition.entrySet()) {
            entry.setValue(rootPath + File.separator + entry.getValue());
        }
    }

    /**
     * Adds a new predefined grammar (including its field maps) to the list of utilised grammars.
     * If no predefined grammar is set so far, the necessary list and map is created.
     *
     * @param name,               the name of the predefined grammar
     * @param renameFileLocation, the location of the file defining the map from fields of the predefined
     *                            grammar to those of the analysed data structure.
     */
    public void addPredefinedGrammar(String name, String renameFileLocation) {

        if (this.usedPredefinedGrammars == null) {
            this.usedPredefinedGrammars = new ArrayList<>();
            this.grammar2RenameMap = new LinkedHashMap<>();
        }

        this.usedPredefinedGrammars.add(name);
        this.pathsToGrammar2RenameDefinition.put(name, renameFileLocation);
    }

    public ArrayList<String> getUsedPredefinedGrammars() {

        return usedPredefinedGrammars;
    }

    public String getRenamingLocation(String predefinedGrammar) {

        return this.pathsToGrammar2RenameDefinition.get(predefinedGrammar);
    }

    public String getPathToSettingsFile() {
        return pathToSettingsFile;
    }

    public void setPathToSettingsFile(String pathToSettingsFile) {
        this.pathToSettingsFile = pathToSettingsFile;
    }
}
