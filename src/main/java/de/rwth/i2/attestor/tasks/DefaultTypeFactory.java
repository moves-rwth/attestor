package de.rwth.i2.attestor.tasks;

import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;

public class DefaultTypeFactory extends TypeFactory {

	@Override
	public Type getType(String name) {
		
		return GeneralType.getType(name);
	}

	
}
