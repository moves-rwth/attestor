package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONWriter;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.procedures.Contract;

public class JsonContractExporter {

	
	 public void export(String directory, Map<String,Collection<Contract>> contracts) throws IOException {

	        FileUtils.createDirectories(directory);
	        FileWriter writer = new FileWriter(directory + File.separator + "contractExport.json");
	        exportContracts(writer, contracts);
	        writer.close();

	        for ( Entry<String, Collection<Contract>> entry : contracts.entrySet() ) {
	        	
	        	String signature = entry.getKey();
	        	signature = signature.replaceAll("[^A-Za-z0-9]", "");
	        	Collection<Contract> contractsForMethod = entry.getValue();
	        	
	        	int prCount = 1;
	        	for( Contract contract : contractsForMethod ){
	        		
	        		HeapConfiguration precondition = contract.getPrecondition();
	        		
					exportHeapConfiguration(directory + File.separator + signature + "pr" + prCount + ".json",
	                        precondition);
					
					int poCount = 1;
					for( HeapConfiguration postcondition : contract.getPostconditions()  ){
						exportHeapConfiguration(
								directory + File.separator +signature+"po"+prCount+"-"+poCount+".json",
		                        postcondition );
						poCount++;
					}
					prCount++;
	        	}	   
	        }

	    }
	 
	 void exportContracts(FileWriter writer, Map<String, Collection<Contract>> contracts) {
	        JSONWriter jsonWriter = new JSONWriter(writer);

	        jsonWriter.array();
	        for ( Entry<String, Collection<Contract>> entry : contracts.entrySet() ) {
	        	
	        	String signature = entry.getKey();
	        	signature = signature.replaceAll("[^A-Za-z0-9]", "");
	        	Collection<Contract> contractsForMethod = entry.getValue();
	        	
	            jsonWriter.object()
                .key("nonterminal").value(signature)
                .key("numberOfContracts").value(contractsForMethod.size())
                .key("contracts").array();
	        	
	            int prCount = 1;
	        	for( Contract contract : contractsForMethod ){
	        		
	            int numberOfPostConditions = contract.getPostconditions().size();

	            jsonWriter.object()
	                    .key("precondition").value("pr" + prCount)
	                    .key("numberOfPostconditions").value(numberOfPostConditions)
	                    .key("postconditions").array();
	            for (int poCount = 1; poCount <= numberOfPostConditions; poCount++) {

	                jsonWriter.value("po" + prCount + "-" + poCount );

	            }
	            
	            jsonWriter.endArray() //postconditions
	            .endObject(); //contract
	            
	            prCount++;
	        	}
	            jsonWriter.endArray() //contracts
	            .endObject(); //method
	        }
	        jsonWriter.endArray(); //methods
		
	}

		private void exportHeapConfiguration(String filename, HeapConfiguration hc)
	            throws IOException {

	        FileWriter writer = new FileWriter(filename);
	        HeapConfigurationExporter exporter = new JsonExtendedHeapConfigurationExporter(writer);
	        exporter.export(hc);
	        writer.close();
	    }

}
