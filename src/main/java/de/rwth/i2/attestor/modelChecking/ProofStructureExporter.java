package de.rwth.i2.attestor.modelChecking;

import java.io.IOException;

public interface ProofStructureExporter {

    void export(String name, ProofStructure ps) throws IOException;

}
