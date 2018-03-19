package de.rwth.i2.attestor.io.jsonImport;

public interface HeapConfigurationRenaming {

    String getTypeRenaming(String typeName);

    String getSelectorRenaming(String typeName, String selector);
}
