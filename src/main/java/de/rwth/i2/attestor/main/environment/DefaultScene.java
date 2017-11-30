package de.rwth.i2.attestor.main.environment;

import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

public class DefaultScene implements Scene {

    private final GeneralType.GeneralTypeFactory typeFactory = new GeneralType.GeneralTypeFactory();

    @Override
    public Type getType(String name) {
        return typeFactory.get(name);
    }
}
