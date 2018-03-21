package de.rwth.i2.attestor.phases.communication;

import java.io.File;

/**
 * All communication related to exporting artifacts.
 *
 * @author Hannah Arndt, Christoph
 */
public class OutputSettings {


    private String rootPath = "";

    private String exportPath = null;

    private String exportGrammarPath = null;

    private String exportLargeStatesPath = null;

    private String saveContractsPath = null;

    private String exportContractsPath = null;

    public void setRootPath(String rootPath) {

        this.rootPath = rootPath;
    }


    public String getExportPath() {

        if(exportPath == null) {
            return null;
        }
        return getRootPath() + exportPath;
    }

    private String getRootPath() {

        if(rootPath.isEmpty()) {
            return rootPath;
        }
        return rootPath + File.separator;
    }


    public void setExportLargeStatesPath(String exportLargeStatesPath) {

        this.exportLargeStatesPath = exportLargeStatesPath;
    }

    public String getExportLargeStatesPath() {

        if(exportLargeStatesPath == null) {
            return null;
        }
        return getRootPath() + exportLargeStatesPath;
    }

    public void setExportGrammarPath(String exportGrammarPath) {

        this.exportGrammarPath = exportGrammarPath;
    }

    public void setExportPath(String exportPath) {

        this.exportPath = exportPath;
    }

    public String getExportGrammarPath() {

        if(exportGrammarPath == null) {
            return null;
        }
        return getRootPath() + exportGrammarPath;
    }

    public void setSaveContractsPath(String saveContractsPath) {

        this.saveContractsPath = saveContractsPath;
    }

    public String getSaveContractsPath() {

        if(saveContractsPath == null) {
            return null;
        }
        return getRootPath() + saveContractsPath;
    }

    public void setExportContractsPath(String exportContractsPath) {

        this.exportContractsPath = exportContractsPath;
    }

    public String getExportContractsPath() {

        if(exportContractsPath == null) {
            return null;
        }
        return getRootPath() + exportContractsPath;
    }
}
