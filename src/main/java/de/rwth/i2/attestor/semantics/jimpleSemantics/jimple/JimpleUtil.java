
package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;

import de.rwth.i2.attestor.semantics.jimpleSemantics.JimpleProgramState;
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
	
	
    public static JimpleProgramState shallowCopyExecutable(JimpleProgramState programState) {

        return (JimpleProgramState) programState.shallowCopy();
	}

	public static JimpleProgramState updatePC(JimpleProgramState programState, int nextPC) {
		
		JimpleProgramState shallow = shallowCopyExecutable(programState);
		shallow.setProgramCounter(nextPC);
		return shallow;
	}
	
	public static JimpleProgramState deepCopy(JimpleProgramState programState) {

        return programState.clone();
	}
	
	public static Set<ProgramState> createSingletonAndUpdatePC(JimpleProgramState programState, int nextPC) {
		
		JimpleProgramState result = JimpleUtil.updatePC(programState, nextPC);
		return SingleElementUtil.createSet( result );
	}
	
}
