package de.rwth.i2.attestor.main.phases.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.FileReader;
import de.rwth.i2.attestor.io.jsonImport.JsonImporter;
import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.main.phases.AbstractPhase;

public class ParseContractsPhase extends AbstractPhase {

	@Override
	public String getName() {
		return "Parse contracts";
	}

	@Override
	protected void executePhase() {
		ArrayList<String> fileNames = settings.input().getContractFileNames();
		if( ! fileNames.isEmpty() ){
			String path = settings.input().getPathToContracts();
			for( String fileName : fileNames ){
				loadContract( path, fileName );
			}
		}

	}

	private void loadContract(String path, String filename) {
    
        try {
            String str = FileReader.read( path + File.separator +filename );
    
            JSONObject obj = new JSONObject(str);
            String signature = obj.getString("method");
            IpaAbstractMethod abstractMethod = IpaAbstractMethod.getMethod(signature);
            
            Consumer<String> addUsedSelectorLabel = settings.input()::addUsedSelectorLabel;
            JSONArray array = obj.getJSONArray("contracts");
            for( int i = 0; i < array.length(); i++ ){
            JSONObject contract = array.getJSONObject(i);
            final JSONObject jsonPrecondition = contract.getJSONObject("precondition");
			HeapConfiguration precondition = JsonImporter.parseHC(jsonPrecondition, addUsedSelectorLabel );
            
			List<HeapConfiguration> postconditions = new ArrayList<>();
			JSONArray jsonPostConditions = contract.getJSONArray("postconditions");
			for( int p = 0; p < jsonPostConditions.length(); p++ ){
				final JSONObject jsonPostcondition = jsonPostConditions.getJSONObject(p);
				postconditions.add( JsonImporter.parseHC(jsonPostcondition, addUsedSelectorLabel));
			}
				abstractMethod.addContracts(precondition, postconditions);
            }


        } catch (FileNotFoundException e) {
            logger.error("Could not parse contract at location " + path + File.separator + filename + ". Skipping it.");
        }		
	}

	@Override
	public void logSummary() {
		// nothing to report

	}

	@Override
	public boolean isVerificationPhase() {
		return false;
	}

}
