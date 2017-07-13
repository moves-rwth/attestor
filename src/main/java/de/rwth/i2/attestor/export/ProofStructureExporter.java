package de.rwth.i2.attestor.export;

import de.rwth.i2.attestor.modelChecking.ProofStructure;

public interface ProofStructureExporter {

	void export(String name, ProofStructure ps);
	
	void close();
}
