package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;


import java.util.Set;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;

/**
 * An auxiliary class to remove dead variables from a given executable.
 */
public class TemporaryVariablesUtil {

    /**
     * Removes all dead variables from a given expression.
     * @param name A string encoding of the expression whose dead variables should be removed.
     * @param executable The executable in which dead variables should be removed.
     * @param liveVariables A list of live variables for the expression encoded by name.
     */
	public static void checkAndRemoveTemp(String name, JimpleExecutable executable, Set<String> liveVariables) {

		if(Settings.getInstance().options().isRemoveDeadVariables() ) {

			String [] vars = name.split( "(==)|(\\!=)|(=)" );
			for (String var : vars) {
				String varName = var.split("\\.")[0].trim();
				if (!liveVariables.contains(varName) && !executable.isConstantName(varName)) {
					executable.removeVariable(varName);
				}
			}

		}
	}
}
