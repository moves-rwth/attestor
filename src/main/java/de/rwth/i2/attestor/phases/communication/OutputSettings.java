package de.rwth.i2.attestor.phases.communication;

import java.io.File;

/**
 * All communication related to exporting artifacts.
 *
 * @author Hannah Arndt, Christoph
 */
public class OutputSettings {

    /**
     * The path where an exported  state space are stored.
     */
    private String pathForStateSpace;

    /**
     * The path where the exported grammar is stored.
     */
    private String pathForGrammar;

    /**
     * The directory that is created and contains the exported contracts.
     */
    private String pathForContractsForInspection;
    
    /**
     * The path where the exported custom hcs are stored.
     */
    private String pathForCustomHcs;

    /**
     * The directory that is created and contains the exported contracts.
     */
    private String folderForReuseContracts;

    /**
     * If true, no export happens.
     */
    private boolean noExport = false;

    /**
     * @return True if and only if no export should be performed.
     */
    public boolean isNoExport() {

        return noExport;
    }

    /**
     * @param enabled True if no export should be performed.
     */
    public void setNoExport(boolean enabled) {

        noExport = enabled;
    }

    public void setRootPath(String rootPath) {

        this.pathForGrammar = rootPath + File.separator + this.pathForGrammar;
        this.pathForStateSpace = rootPath + File.separator + this.pathForStateSpace;
        this.pathForCustomHcs = rootPath + File.separator + this.pathForCustomHcs;
        this.folderForReuseContracts = rootPath + File.separator + this.folderForReuseContracts;
        this.pathForContractsForInspection = rootPath + File.separator + this.pathForContractsForInspection;
    }

}
