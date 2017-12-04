package de.rwth.i2.attestor.main.settings;

import java.io.File;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.jsonImport.HcLabelPair;

/**
 * All settings related to exporting artifacts.
 *
 * @author Hannah Arndt, Christoph
 */
public class OutputSettings {

	/**
	 * The logger of this class.
	 */
	private static final Logger logger = LogManager.getLogger( "OutputSettings" );

    /**
     * True if and only if the generated state space should be exported to a file.
     */
	private boolean exportStateSpace = false;

    /**
     * The path where an exported  state space are stored.
     */
	private String pathForStateSpace;

    /**
     * The directory that is created and containsSubsumingState the exported state space.
     */
	private String folderForStateSpace = "stateSpace";

    /**
     * True if and only if the loaded grammar should be exported.
     */
	private boolean exportGrammar = false;

    /**
     * The path where the exported grammar is stored.
     */
	private String pathForGrammar;

    /**
     * The directory that is created and containsSubsumingState exported grammars.
     */
	private String folderForGrammar = "grammar";

	/**
	 * True if and only if custom hcs should be exported (for debugging purpose).
	 */
	private boolean exportCustomHcs = false;

	/**
	 * The path where the exported custom hcs are stored.
	 */
	private String pathForCustomHcs;

	/**
	 * The directory that is created and containsSubsumingState the exported custom hcs.
	 */
	private String folderForCustomHcs = "debug";

	/**
	 * Holds the prebooked Hcs, that should be exported (for debugging purpose).
	 */
	private List<HcLabelPair> customHcList;

	  /**
     * True if and only if the generated contracts should be exported.
     */
	private boolean exportContracts = false;

	/**
	 * The directory that is created and contains the exported contracts.
	 */
	private String folderForContracts;

	/**
	 * A mapping containing the signatures of those methods the user requests contracts for
	 * and as values the file names these contracts should be written to.
	 */
	private Map<String, String> requiredContracts;

	/**
	 * The directory that contains the case study's output for report generation
	 */
	private String folderForReportOutput;

	/**
	 * True if and only if report output should be generated.
	 */
	private boolean exportReportOutput = false;

	/**
     * Sets the default path for all exports.
     * @param path The default path.
     */
	public void setDefaultPath( String path ){
		pathForStateSpace = path;
		pathForGrammar = path;
		pathForCustomHcs = path;
		folderForReportOutput = path;
	}

    /**
     * @return True if and only if the generated state space should be exported.
     */
	public boolean isExportStateSpace() {
		return exportStateSpace;
	}

    /**
     * @param exportStateSpace True if and only if the generated state space should be exported.
     */
	public void setExportStateSpace(boolean exportStateSpace) {
		this.exportStateSpace = exportStateSpace;
	}

    /**
     *
     * @param pathForStateSpace The path where exported state spaces are stored.
     */
	public void setPathForStateSpace(String pathForStateSpace) {
		this.pathForStateSpace = pathForStateSpace;
	}

    /**
     * @param folderForStateSpace The directory that is created and containsSubsumingState the exported state space.
     */
	public void setFolderForStateSpace(String folderForStateSpace) {
		this.folderForStateSpace = folderForStateSpace;
	}

    /**
     * @return The directory that is created and containsSubsumingState the exported state space.
     */
	public String getLocationForStateSpace(){
		return pathForStateSpace + File.separator + folderForStateSpace;
	}

    /**
     * @return True if and only if loaded grammars should be exported.
     */
	public boolean isExportGrammar() {
		return exportGrammar;
	}

    /**
     * @param exportGrammar True if and only if loaded grammars should be exported.
     */
	public void setExportGrammar(boolean exportGrammar) {
		this.exportGrammar = exportGrammar;
	}

    /**
     * @param pathForGrammar The path where exported grammars are stored.
     */
	public void setPathForGrammar(String pathForGrammar) {
		this.pathForGrammar = pathForGrammar;
	}

    /**
     * @param folderForGrammar The directory containing exported grammars.
     */
	public void setFolderForGrammar(String folderForGrammar) {
		this.folderForGrammar = folderForGrammar;
	}

    /**
     * @return The fully qualified path to the directory containing exported grammars.
     */
	public String getLocationForGrammar(){
		return pathForGrammar + File.separator + folderForGrammar;
	}


	/**
	 * @return True if and only if custom HCs should be exported (for debugging purpose).
	 */
	public boolean isExportCustomHcs() {
		return exportCustomHcs;
	}

	/**
	 * @param exportCustomHcs True if and only if the generated state space should be exported.
	 */
	public void setExportCustomHcs(boolean exportCustomHcs) {

		this.exportCustomHcs = exportCustomHcs;

		if(exportCustomHcs){
			this.customHcList = new ArrayList<>();
		}
	}

	/**
	 * Adds a new graph to the list of custom graphs, that should be exported for debugging purpose.
	 * @param label, the identifier of the graphs
	 * @param hc, the graph itself
	 */
	public void addCustomHc(String label, HeapConfiguration hc){

		if(customHcList != null){
			customHcList.add(new HcLabelPair(label, hc));
		} else {
			logger.warn("Adding an HC for custom debug export, although the corresponding export options is disabled!");
		}
	}

	/**
	 *
	 * @param pathForCustomHcs The path where exported custom hcs are stored.
	 */
	public void setPathForCustomHcs(String pathForCustomHcs) {
		this.pathForCustomHcs = pathForCustomHcs;
	}

	/**
	 * @param folderForCustomHcs The directory that is created and containsSubsumingState the exported custom hcs.
	 */
	public void setFolderForCustomHcs(String folderForCustomHcs) {
		this.folderForCustomHcs = folderForCustomHcs;
	}

	/**
	 * @return The directory that is created and containsSubsumingState the exported state space.
	 */
	public String getLocationForCustomHcs(){
		return pathForCustomHcs + File.separator + folderForCustomHcs;
	}
	
	/**
	 * @param exportCustomHcs True if and only if the generated contracts should be exported.
	 */
	public boolean isExportContracts() {
		return exportContracts;
	}
	
	/**
	 * @param exportContracts true if and only if (some) contracts should be exported.
	 */
	public void setExportContracts(boolean exportContracts) {
		this.exportContracts = exportContracts;

		if(exportContracts){
			this.requiredContracts = new HashMap<>();
		}
	}
	
	/**
	 * @return The directory that is created and contains the exported contracts.
	 */
	public String getDirectoryForContracts() {
		return this.folderForContracts;
	}
	
	/**
	 * Defines where the exported contracts should be stored
	 * @param directory
	 */
	public void setDirectoryForContracts( String directory ) {
		this.folderForContracts = directory;
	}

	/**
	 * A mapping from the signatures for which contracts are requested
	 * and the names of the files the exported contracts are stored in.
	 * @return
	 */
	public Map<String,String> getContractRequests() {
		return this.requiredContracts;
	}
	
	/**
	 * Adds a pair requested signature and filename to the set of requested contracts
	 * @param signature the signature of the method whose contract is requested
	 * @param filename the name of the file where the contract should be written to.
	 */
	public void addRequiredContract( String signature, String filename ) {
		this.requiredContracts.put(signature, filename + ".json");
	}

    /**
     * Checks whether all necessary paths and names are present for objects that should be exported.
     * @return True if and only if the current settings are consistent in the above sense.
     */
	public boolean isValid(){
		
		if( exportGrammar ){
			if( pathForGrammar == null || folderForGrammar == null ){
				return false;
			}
		}
		if( exportStateSpace ){
			if( pathForStateSpace == null || folderForStateSpace == null ){
				return false;
			}
		}
		if( exportCustomHcs ){
			if( pathForCustomHcs == null || folderForCustomHcs == null ){
				return false;
			}
		}
		return true;
	}

	public void setRootPath(String rootPath) {
		this.pathForGrammar = rootPath + File.separator + this.pathForGrammar;
		this.pathForStateSpace = rootPath + File.separator + this.pathForStateSpace;
		this.pathForCustomHcs = rootPath + File.separator + this.pathForCustomHcs;
		this.folderForContracts = rootPath + File.separator + this.folderForContracts;
	}

	public List<HcLabelPair> getCustomHcSet() {
		return this.customHcList;
	}

	public String getFolderForReportOutput() {
		return folderForReportOutput;
	}

	public void setFolderForReportOutput(String folderForReportOutput) {
		this.folderForReportOutput = folderForReportOutput;
	}

	public boolean isExportReportOutput() {
		return exportReportOutput;
	}

	public void setExportReportOutput(boolean exportReportOutput) {
		this.exportReportOutput = exportReportOutput;
	}


}
