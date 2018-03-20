package de.rwth.i2.attestor.semantics.util;


import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

import java.util.Set;

/**
 * An auxiliary class to remove dead variables from a given executable.
 */
public class DeadVariableEliminator {

    /**
     * Removes all dead variables from a given expression.
     *
     * @param sceneObject   Object in the current scene.
     * @param name          A string encoding of the expression whose dead variables should be removed.
     * @param programState  The programState in which dead variables should be removed.
     * @param liveVariables A list of live variables for the expression encoded by name.
     */
    public static void removeDeadVariables(SceneObject sceneObject, String name,
                                           ProgramState programState, Set<String> liveVariables) {

        String[] vars = name.split("(==)|(!=)|(=)");
        for (String var : vars) {
            String varName = var.split("\\.")[0].trim();
            if (!liveVariables.contains(varName)
                    && !Constants.isConstant(varName)
                    && !Markings.isMarking(varName)
                    && !sceneObject.scene().labels().isKeptVariableName(varName)
                    ) {
                programState.removeVariable(varName);
            }
        }
    }
}
