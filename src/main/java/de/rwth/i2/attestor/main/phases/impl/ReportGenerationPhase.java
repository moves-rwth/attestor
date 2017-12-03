package de.rwth.i2.attestor.main.phases.impl;

import java.io.*;
import java.util.Set;

import de.rwth.i2.attestor.grammar.GrammarExporter;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat.*;
import de.rwth.i2.attestor.io.jsonExport.inputFormat.ContractToInputFormatExporter;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.ipa.IpaContractCollection;
import de.rwth.i2.attestor.main.environment.Scene;
import de.rwth.i2.attestor.main.phases.AbstractPhase;
import de.rwth.i2.attestor.main.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.main.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.main.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.ZipUtils;

public class ReportGenerationPhase extends AbstractPhase {

    private StateSpace stateSpace;
    private Program program;

    public ReportGenerationPhase(Scene scene) {
        super(scene);
    }

    @Override
    public String getName() {

        return "Report generation";
    }

    @Override
    protected void executePhase() {

        if(settings.output().isNoExport()) {
            return;
        }

        stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        program = getPhase(ProgramTransformer.class).getProgram();

        try {
            if (settings.output().isExportGrammar()) {
                exportGrammar();
            }

            if (settings.output().isExportStateSpace()) {
                exportStateSpace();
            }

            if (settings.output().isExportCustomHcs()) {
                exportCustomHcs();
            }
            
            if( settings.output().isExportContracts() ) {
            	exportContracts();
            }
            
        } catch(IOException e) {
            throw new IllegalStateException(e.getMessage());
        }

    }
    
    private void exportContracts() throws IOException {
    	String directory = settings.output().getDirectoryForContracts();
    	FileUtils.createDirectories( directory );
    	for( String signature : settings.output().getContractRequests().keySet() ) {
    		
    		 String filename = settings.output().getContractRequests().get( signature );
    		 FileWriter writer = new FileWriter( directory + File.separator + filename);
    		 
    		 IpaContractCollection contracts = scene().getMethod(signature).getContracts();
    		 
    		 ContractToInputFormatExporter exporter = new ContractToInputFormatExporter(writer);
    	     exporter.export( signature, contracts );
    	     writer.close();
    	}
    }
    
    private void exportCustomHcs() throws IOException {

        String location = settings.output().getLocationForCustomHcs();

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("customHcViewer" +
                ".zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        // Generate JSON files for prebooked HCs and their summary
        CustomHcListExporter exporter = new JsonCustomHcListExporter();
        exporter.export(location + File.separator + "customHcsData", settings.output().getCustomHcSet());

        logger.info("Custom HCs exported to '"
                + location
        );
    }

    private void exportStateSpace() throws IOException {

        logger.info("Exporting state space...");
        String location = settings.output().getLocationForStateSpace();

        exportStateSpace(
                location + File.separator + "data",
                stateSpace,
                program
        );

        Set<ProgramState> states = stateSpace.getStates();
        for(ProgramState state : states) {
            int i = state.getStateSpaceId();
            exportHeapConfiguration(
                    location + File.separator + "data",
                    "hc_" + i + ".json",
                    state.getHeap()
            );
        }

        InputStream zis = getClass().getClassLoader().getResourceAsStream("viewer.zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        logger.info("done. State space exported to '"
                + location
                + "'"
        );
    }

    private void exportGrammar() throws IOException {

        logger.info("Exporting grammar...");

        String location = settings.output().getLocationForGrammar();

        // Copy necessary libraries
        InputStream zis = getClass().getClassLoader().getResourceAsStream("grammarViewer" +
                ".zip");

        File targetDirectory = new File(location + File.separator);
        ZipUtils.unzip(zis, targetDirectory);

        // Generate JSON files
        GrammarExporter exporter = new JsonGrammarExporter();
        exporter.export(location + File.separator + "grammarData",
                getPhase(GrammarTransformer.class).getGrammar());

        logger.info("done. Grammar exported to '" + location + "'");
    }

    private void exportHeapConfiguration(String directory, String filename, HeapConfiguration hc)
            throws IOException {

        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + filename);
        HeapConfigurationExporter exporter = new JsonHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
    }

    private void exportStateSpace(String directory, StateSpace stateSpace, Program program)
            throws IOException {

        FileUtils.createDirectories(directory);
        Writer writer = new BufferedWriter(
                new OutputStreamWriter( new FileOutputStream(directory + File.separator + "statespace.json") )
        );
        StateSpaceExporter exporter = new JsonStateSpaceExporter(writer);
        exporter.export(stateSpace, program);
        writer.close();
    }

    @Override
    public void logSummary() {

        if(!settings.output().isNoExport() && settings.output().isExportStateSpace()) {
            String location = settings.output().getLocationForStateSpace();
            logSum("State space exported to '"
                    + location
                    + "'"
            );
        }
    }

    @Override
    public boolean isVerificationPhase() {

        return false;
    }
}
