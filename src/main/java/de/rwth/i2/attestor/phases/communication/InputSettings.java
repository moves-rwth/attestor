package de.rwth.i2.attestor.phases.communication;

import de.rwth.i2.attestor.io.jsonImport.HeapConfigurationRenaming;

import java.io.File;
import java.util.*;

/**
 * All global communication regarding input files.
 *
 * @author Christoph, Christina
 */
public class InputSettings implements HeapConfigurationRenaming {


    /**
     * A common path that is the prefix of all other paths to input/output files.
     */
    private String rootPath = "";

    /**
     * Short human-readable description of the analysis
     */
    private String description;
    
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
    private String methodName = "main";

    private Set<String> predefinedGrammarNames = new LinkedHashSet<>();

    private Map<String, String> typeRenaming = new LinkedHashMap<>();

    private Map<String, Map<String, String>> selectorRenaming = new LinkedHashMap<>();

    /**
     * filenames containing user defined contracts to use
     */
    ArrayList<String> contractFiles = new ArrayList<>();
    private List<String> userDefinedGrammarFiles = new ArrayList<>();
    private List<String> initialHeapFiles = new ArrayList<>();

    public String getRootPath() {

        if(rootPath == "") {
            return rootPath;
        }
        return rootPath + File.separator;
    }

    public void addPredefinedGrammarName(String name) {

        predefinedGrammarNames.add(name);
    }

    public Collection<String> getPredefinedGrammarNames() {

        return predefinedGrammarNames;
    }

    public void addTypeRenaming(String from, String to) {

        typeRenaming.put(from, to);
    }

    public void addSelectorRenaming(String typeName, String from, String to) {

        if(!selectorRenaming.containsKey(typeName)) {
            selectorRenaming.put(typeName, new LinkedHashMap<>());
        }
        Map<String, String> map = selectorRenaming.get(typeName);
        map.put(from, to);
    }

    @Override
    public String getTypeRenaming(String typeName) {

        return typeRenaming.getOrDefault(typeName, typeName);
    }

    @Override
    public String getSelectorRenaming(String typeName, String selector) {

        return selectorRenaming.getOrDefault(typeName, Collections.singletonMap(selector, selector))
                .getOrDefault(selector, selector);
    }

    /**
     * @return The path to the classes that are analyzed.
     */
    public String getClasspath() {

        return getRootPath() + classpath;
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
    public Collection<String> getContractFileNames() {

        List<String> result = new ArrayList<>();
        for(String s : contractFiles) {
            result.add(getRootPath() + s);
        }
        return result;
    }
    
   
    /**
     * @param rootPath a path prefix which should be applied to all user defined paths
     */
    public void setRootPath(String rootPath) {

        this.rootPath = rootPath;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addUserDefinedGrammarFile(String userDefinedGrammarFile) {

        this.userDefinedGrammarFiles.add(userDefinedGrammarFile);
    }

    public Collection<String> getUserDefinedGrammarFiles() {

        List<String> result = new ArrayList<>();
        for(String s : userDefinedGrammarFiles) {
            result.add(getRootPath() + s);
        }
        return result;
    }

    public void addInitialHeapFile(String initialHeapFile) {

        this.initialHeapFiles.add(initialHeapFile);
    }

    public List<String> getInitialHeapFiles() {

        List<String> result = new ArrayList<>();
        for(String s : initialHeapFiles) {
            result.add(getRootPath() + s);
        }
        return result;
    }

}
