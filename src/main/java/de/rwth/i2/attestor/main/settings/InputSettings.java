package de.rwth.i2.attestor.main.settings;

import de.rwth.i2.attestor.markings.Marking;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * All global settings regarding input files.
 *
 * @author Christoph, Christina
 */
public class InputSettings {

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
	private HashMap<String,String> pathsToGrammar2RenameDefininition = new HashMap<>();


	/**
	 * The mapping from predefined grammars to their rename mapping
	 */
	private HashMap<String, HashMap<String, String>> grammar2RenameMap;

    /**
     * The path to the file specifying the initial state.
     */
	private String pathToInput;

	/**
	 * The marking used for preprocessing to track object identities
	 */
	private Marking marking;

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

    /**
     * Sets the default path to search for all possible input files.
     * @param path The default path.
     */
	public void setDefaultPath( String path ){
		classpath = path;
		pathToGrammar = path;
		pathToInput = path;
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
     * @param pathToInput The directory that contains the file holding the initial state.
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
			this.usedPredefinedGrammars = new ArrayList<String>();
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

	public void setMarking(Marking marking) {
		this.marking = marking;
	}

	public Marking getMarking() {
		return marking;
	}
}
