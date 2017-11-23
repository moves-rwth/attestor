package de.rwth.i2.attestor.main.settings;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * All global settings regarding input files.
 *
 * @author Christoph, Christina
 */
public class InputSettings {

	/**
	 * Description of the analyzed scenario.
	 */
	private String scenario;

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
	 * The paths to the file specifying the renaming used for the 
	 * predefined grammars
	 */
	private final HashMap<String,String> pathsToGrammar2RenameDefininition = new HashMap<>();


	/**
	 * The mapping from predefined grammars to their rename mapping
	 */
	private HashMap<String, HashMap<String, String>> grammar2RenameMap;
	
	/**
	 * path to the files storing user defined contracts
	 */
	private String pathToContracts;
	
	/**
	 * filenames containing user defined contracts to use
	 */
	ArrayList<String> contractFiles = new ArrayList<>();

    /**
     * The path to the file specifying the initial state.
     */
	private String pathToInput;

	public URL getInitialStatesURL() {
		return initialStatesURL;
	}

	/**
	 * The url to the default empty initial state.
	 */
	public URL initialStatesURL;

	public String getInputName() {
		return inputName;
	}

	/**
     * The name of the file specifying the initial state.
     */
	private String inputName;

	private final Set<String>	usedSelectorLabels = new HashSet<>();
	private final Set<String> grammarSelectorLabels = new HashSet<>();

	/**
	 * Selector labels corresponding to variables of primitive types.
	 */
	private final Set<String> primitiveSelectorLabels = new HashSet<>();

	public void addPrimitiveSelectorLabel(String label) {
		primitiveSelectorLabels.add(label);
	}

	public boolean isPrimitiveSelectorLabel(String label) {
		return primitiveSelectorLabels.contains(label);
	}

    /**
     * Sets the default path to search for all possible input files.
     * @param path The default path.
     */
	public void setDefaultPath( String path ){
		classpath = path;
		pathToGrammar = path;
		pathToInput = path;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public String getScenario() {
		return scenario;
	}

    /**
     * @return The path to the classes that are analyzed.
     */
	public String getClasspath() {
		return classpath;
	}

    /**
     * Sets the path to the classes that are analyzed.
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
      * @param methodName The name of the method to analyze.
     */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

    /**
     * Sets the path to the file containing the graph grammar underlying the analysis.
     * @param pathToGrammar The path to the file containing the graph grammar.
     */
	public void setPathToGrammar(String pathToGrammar) {
		this.pathToGrammar = pathToGrammar;
	}

    /**
     * Returns the name of the file containing the user-defined graph grammar underlying the analysis.
     * @return the location of the userDefinedGrammar
     */
	public String getUserDefinedGrammarName() {
		return this.userDefinedGrammarName;
	}
	
	/**
	 * Sets the path to the files containing user defined contracts
	 */
	public void setPathToContracts(String pathToContracts) {
		this.pathToContracts = pathToContracts;
	}
	
	/**
	 * Returns the path to the files containing user defined contracts
	 * @return the location of the user defined contracts
	 */
	public String getPathToContracts(){
		return this.pathToContracts;
	}
	
	/**
	 * Adds the provided name to the list of contract files to consider for this run
	 * @param name the filename
	 */
	public void addContractFile( String name ){
		this.contractFiles.add( name );
	}
	/**
	 * Returns the filenames of the user defined contracts to use
	 * @return a list containing the filenames
	 */
	public ArrayList<String> getContractFileNames(){
		return this.contractFiles;
	}

	/**
	 * Sets the name of the file containing the user-defined graph grammar underlying the analysis.
	 * @param userDefinedGrammarName The name of the file containing the graph grammar.
	 */
	public void setUserDefinedGrammarName(String userDefinedGrammarName) {
		this.userDefinedGrammarName = userDefinedGrammarName;
	}

    /**
     * @return The fully qualified path to the file holding the graph grammar underlying the analysis.
     */
	public String getGrammarLocation(){
		return pathToGrammar + File.separator + userDefinedGrammarName;
	}

    /**
     * Sets the path to the file holding the initial state.
     * @param pathToInput The directory that containsSubsumingState the file holding the initial state.
     */
	public void setPathToInput(String pathToInput) {
		this.pathToInput = pathToInput;
	}

    /**
     * Sets the name of the file holding the initial state.
     * @param inputName The name of the file holding the initial state.
     */
	public void setInputName(String inputName) {
		this.inputName = inputName;
	}

    /**
     * @return The fully qualified path to the file holding the initial state.
     */
	public String getInputLocation(){
		return pathToInput + File.separator + inputName;
	}

	/**
     * @return Checks whether all paths and file names, i.e. grammar, class, classpath, method, and initial state,
     *         have been set.
     */
	public boolean isComplete() {
		return className != null && classpath != null && methodName != null
				&& pathToGrammar != null && userDefinedGrammarName != null
				&& pathToInput != null && inputName != null;
	}

	public void setRootPath(String rootPath) {
		this.classpath = rootPath + File.separator +  this.classpath;
		this.pathToGrammar = rootPath + File.separator + this.pathToGrammar;
		this.pathToInput = rootPath + File.separator + this.pathToInput;
		this.pathToContracts = rootPath + File.separator + this.pathToContracts;
		for( java.util.Map.Entry<String, String> entry : pathsToGrammar2RenameDefininition.entrySet() ){
			entry.setValue( rootPath + File.separator + entry.getValue() );
		}
	}

	/**
	 * Adds a new predefined grammar (including its field maps) to the list of utilised grammars.
	 * If no predefined grammar is set so far, the necessary list and map is created.
	 * @param name, the name of the predefined grammar
	 * @param renameFileLocation, the location of the file defining the map from fields of the predefined 
	 *  grammar to those of the analysed data structure.
	 */
	public void addPredefinedGrammar(String name, String renameFileLocation){
		if(this.usedPredefinedGrammars == null){
			this.usedPredefinedGrammars = new ArrayList<>();
			this.grammar2RenameMap = new HashMap<>();
		}

		this.usedPredefinedGrammars.add( name );
		this.pathsToGrammar2RenameDefininition.put(name, renameFileLocation);
	}

	public ArrayList<String> getUsedPredefinedGrammars() {
		return usedPredefinedGrammars;
	}


	public HashMap<String,String> getRenaming(String predefinedGrammar) {
		return this.grammar2RenameMap.get(predefinedGrammar);
	}

	public void setInitialStatesURL(URL resource) {
		this.initialStatesURL = resource;
	}

	public String getRenamingLocation(String predefinedGrammar) {
		return this.pathsToGrammar2RenameDefininition.get( predefinedGrammar );
	}

	public void addGrammarSelectorLabel(String selector) {
		grammarSelectorLabels.add(selector);
	}

	public void addUsedSelectorLabel(String selector) {
		usedSelectorLabels.add(selector);
	}

	public Set<String> getGrammarSelectorLabels() {
		return grammarSelectorLabels;
	}

	public Set<String> getUsedSelectorLabels() {
		return usedSelectorLabels;
	}





}
