package de.rwth.i2.attestor.phases.communication;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * All communication related to exporting artifacts.
 *
 * @author Hannah Arndt, Christoph
 */
// TODO
public class OutputSettings {

    /**
     * True if and only if the generated contracts should be exported.
     */
    private boolean exportContractsForInspection = false;

    /**
     * The directory that is created and contains the exported contracts.
     */
    private String pathForContractsForInspection;
    
    /**
     * The directory that is created and contains contracts.
     */
    private String folderForContractsForInspection = "contracts";

    /**
     * True if and only if the generated contracts should be exported.
     */
    private boolean exportContractsForReuse = false;

    /**
     * The directory that is created and contains the exported contracts.
     */
    private String folderForReuseContracts;

    /**
     * A mapping containing the signatures of those methodExecution the user requests contracts for
     * and as values the file names these contracts should be written to.
     */
    private Map<String, String> requiredContractsForReuse;

    // -----------------------------------------------------------------
    private String rootPath = "";

    private String exportPath = null;

    private String exportGrammarPath = null;

    private String exportLargeStatesPath = null;
    // -----------------------------------------------------------------


    /**
     * @return True if and only if the generated state space should be exported.
     */
    public boolean isExportStateSpace() {

        return exportPath != null;
    }

    private String getRootPath() {

        if(rootPath.isEmpty()) {
            return rootPath;
        }
        return rootPath + File.separator;
    }

    public String getExportPath() {

        return getRootPath() + exportPath;
    }

    /**
     * @return True if and only if loaded grammars should be exported.
     */
    public boolean isExportGrammar() {

        return exportGrammarPath != null;
    }

    /**
     * @return True if and only if used contracts should be exported.
     */
    public boolean isExportContractsForInspection() {

        return exportContractsForInspection;
    }

    /**
     * @return The fully qualified path to the directory containing exported contracts for inspection.
     */
    public String getLocationForContractsForInspection() {

        return pathForContractsForInspection + File.separator + folderForContractsForInspection;
    }

    public boolean isExportContractsForReuse() {

        return getRequiredSavedContracts().isEmpty();
    }

    public void setRootPath(String rootPath) {

        this.rootPath = rootPath;
    }

    public String getExportLargeStatesPath() {
        return exportLargeStatesPath;
    }

    public void setExportLargeStatesPath(String exportLargeStatesPath) {
        this.exportLargeStatesPath = exportLargeStatesPath;
    }

    public String getExportGrammarPath() {
        return exportGrammarPath;
    }

    public void setExportGrammarPath(String exportGrammarPath) {
        this.exportGrammarPath = exportGrammarPath;
    }

    public void setExportPath(String exportPath) {
        this.exportPath = exportPath;
    }

    public void addSaveContracts(String method, String path) {
    }

    public void addExportContracts(String method, String path) {
    }

    public Map<String, String> getRequiredSavedContracts() {
        return new LinkedHashMap<>(); // TODO
    }

    public Map<String, String> getRequiredExportedContracts() {
        return new LinkedHashMap<>(); // TODO
    }
}
