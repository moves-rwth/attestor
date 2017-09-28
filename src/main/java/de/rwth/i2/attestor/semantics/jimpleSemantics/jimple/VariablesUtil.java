package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;


import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.main.settings.StateSpaceGenerationSettings;
import de.rwth.i2.attestor.markings.Markings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.strategies.VariableScopes;

import java.util.Set;

/**
 * An auxiliary class to remove dead variables from a given executable.
 */
public class VariablesUtil {

    /**
     * Removes all dead variables from a given expression.
     * @param name A string encoding of the expression whose dead variables should be removed.
     * @param programState The programState in which dead variables should be removed.
     * @param liveVariables A list of live variables for the expression encoded by name.
     */
	public static void removeDeadVariables(String name, JimpleProgramState programState, Set<String> liveVariables) {

        StateSpaceGenerationSettings settings = Settings.getInstance().stateSpaceGeneration();

        String [] vars = name.split( "(==)|(!=)|(=)" );
		for (String var : vars) {
            String varName = var.split("\\.")[0].trim();
            if (!liveVariables.contains(varName)
                    && !Constants.isConstant(varName)
                    && !Markings.isMarking(varName)
                    && !settings.isKeptVariableName(VariableScopes.getName(varName))
                    ) {
                programState.removeVariable(varName);
            }
        }
	}
}
