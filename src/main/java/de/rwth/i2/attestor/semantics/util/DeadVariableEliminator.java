package de.rwth.i2.attestor.semantics.util;


import de.rwth.i2.attestor.main.environment.SceneObject;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.StateSpaceGenerationSettings;
import de.rwth.i2.attestor.markings.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.semantics.util.VariableScopes;

import java.util.Set;

/**
 * An auxiliary class to remove dead variables from a given executable.
 */
public class DeadVariableEliminator {

    /**
     * Removes all dead variables from a given expression.
     * @param sceneObject Object in the current scene.
     * @param name A string encoding of the expression whose dead variables should be removed.
     * @param programState The programState in which dead variables should be removed.
     * @param liveVariables A list of live variables for the expression encoded by name.
     */
	public static void removeDeadVariables(SceneObject sceneObject, String name,
                                           ProgramState programState, Set<String> liveVariables) {

        String [] vars = name.split( "(==)|(!=)|(=)" );
		for (String var : vars) {
            String varName = var.split("\\.")[0].trim();
            if (!liveVariables.contains(varName)
                    && !Constants.isConstant(varName)
                    && !Markings.isMarking(varName)
                    && !sceneObject.scene().options().isKeptVariableName(VariableScopes.getName(varName))
                    ) {
                programState.removeVariable(varName);
            }
        }
	}
}
