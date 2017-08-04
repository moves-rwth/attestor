package de.rwth.i2.attestor.types;

public class DefaultTypeFactory extends TypeFactory {

	@Override
	public Type getType(String name) {
		
		return GeneralType.getType(name);
	}

	
}
