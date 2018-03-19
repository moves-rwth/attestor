package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.io.jsonImport.HeapConfigurationRenaming;

public class MockupHeapConfigurationRenaming implements HeapConfigurationRenaming {

    @Override
    public String getTypeRenaming(String typeName) {
        return typeName;
    }

    @Override
    public String getSelectorRenaming(String typeName, String selector) {
        return selector;
    }
}
