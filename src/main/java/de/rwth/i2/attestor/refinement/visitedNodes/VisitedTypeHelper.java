package de.rwth.i2.attestor.refinement.visitedNodes;

import de.rwth.i2.attestor.types.GeneralType;
import de.rwth.i2.attestor.types.Type;

final class VisitedTypeHelper {

    private static final String VISITED_PREFIX = "!";

    static Type getVisitedType(Type type) {

        if(isVisited(type)) {
            return type;
        } else {
            return GeneralType.getType( VISITED_PREFIX + type.toString() );
        }
    }

    static boolean isVisited(Type type) {
        return type.toString().startsWith(VISITED_PREFIX);
    }
}
