
package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleExecutable;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.util.SingleElementUtil;

import java.util.Set;

/**
 * This class should only be used inside of the jimple package.
 * It provides utility functionality that fits nowhere else.
 *
 * @author Hannah Arndt
 */
public class JimpleUtil {
	
	
    public static JimpleExecutable shallowCopyExecutable(JimpleExecutable executable) {

        return (JimpleExecutable) executable.shallowCopy();
	}

	public static JimpleExecutable updatePC(JimpleExecutable executable, int nextPC) {
		
		JimpleExecutable shallow = shallowCopyExecutable(executable);
		shallow.setProgramCounter(nextPC);
		return shallow;
	}
	
	public static JimpleExecutable deepCopy(JimpleExecutable executable) {

        return executable.clone();
	}
	
	public static Set<ProgramState> createSingletonAndUpdatePC(JimpleExecutable executable, int nextPC) {
		
		JimpleExecutable result = JimpleUtil.updatePC(executable, nextPC);
		return SingleElementUtil.createSet( result );
	}
	
}
