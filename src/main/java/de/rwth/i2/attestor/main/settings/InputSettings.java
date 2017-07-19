package de.rwth.i2.attestor.main.settings;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
	private String grammarName;

	// The list of predefined grammars used by the current analysis
	private ArrayList<String> usedPredefinedGrammars;

	// The mapping from predefined grammars to their rename mapping
	private HashMap<String, HashMap<String, String>> grammar2RenameMap;

    /**
     * The path to the file specifying the initial state.
     */
	private String pathToInput;

    /**
     * The name of the file specifying the initial state.
     */
	private String inputName;

    /**
     * Sets the default path to search for all possible input files.
     * @param path The default path.
     */
	public void setDefaultPath( String path){
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
     * Sets the name of the file containing the graph grammar underlying the analysis.
     * @param grammarName The name of the file containing the graph grammar.
     */
	public void setGrammarName(String grammarName) {
		this.grammarName = grammarName;
	}

    /**
     * @return The fully qualified path to the file holding the graph grammar underlying the analysis.
     */
	public String getGrammarLocation(){
		return pathToGrammar + File.separator + grammarName;
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
				&& pathToGrammar != null && grammarName != null
				&& pathToInput != null && inputName != null;
	}

	/**
	 * Adds a new predefined grammar (including its field maps) to the list of utilised grammars.
	 * If no predefined grammar is set so far, the necessary list and map is created.
	 * @param name, the name of the predefined grammar
	 * @param correspondences, the map from fields of the predefined grammar to those of the analysed data structure.
	 */
	public void addPredefinedGrammar(String name, HashMap<String, String> correspondences){
		if(this.usedPredefinedGrammars == null){
			this.usedPredefinedGrammars = new ArrayList<String>();
			this.grammar2RenameMap = new HashMap<>();
		}

		this.usedPredefinedGrammars.add(name);
		this.grammar2RenameMap.put(name, correspondences);
	}

	public ArrayList<String> getUsedPredefinedGrammars() {
		return usedPredefinedGrammars;
	}


	public HashMap<String,String> getRenaming(String predefinedGrammar) {
		return this.grammar2RenameMap.get(predefinedGrammar);
	}
}
