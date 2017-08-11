package de.rwth.i2.attestor.export;

import de.rwth.i2.attestor.modelChecking.ProofStructure;

import java.io.IOException;

public interface ProofStructureExporter {

	void export(String name, ProofStructure ps) throws IOException;
	
	void close();
}
