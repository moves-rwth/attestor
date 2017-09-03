package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.stateSpace.State;

/**
 * An abstraction of a single state in a OldStateSpace.
 * This comprises information about the current program location, that is the value of the program counter,
 * of the state and the program's heap.
 */
public interface ProgramState extends Cloneable, LabelledProgramState, State {

	/**
	 * @return The program location of this state.
	 */
	int getProgramCounter();

	/**
	 * @param pc The program location assigned to this state.
	 */
	void setProgramCounter(int pc);
	
	/**
	 * Adds all required constants if not yet present,
	 * converts variables from the semantics format to the
	 * format used inside the heap, etc.
	 *
	 * @return The program state with the required constants.
	 */
	ProgramState prepareHeap();
	
	/**
	 * Converts the given variable name as it occurs in a program, such as "x",
	 * into a variable name that occurs in the heap configuration corresponding to this
	 * program state. In particular, this includes scope information, such as "0-x".
	 *
	 * @param originalVariableName The variable name as it occurs in the semantics.
	 * @return The corresponding variable name as stored in the heap configuration.
	 */
	String getVariableNameInHeap( String originalVariableName );

	/**
	 * @return The heap configuration determining the heap and the evaluation of variables
	 *         for this program state.
	 */
	HeapConfiguration getHeap();

	/**
	 * @return The size of the heap configuration corresponding to this program state.
	 */
	int size();

	/**
	 * @return A deep copy of this program state.
	 */
	ProgramState clone();

	/**
	 * Creates a shallow copy of this program state in which the underlying heap configuration
	 * is the same as in the copied object.
	 * @return A shallow copy of this program state.
	 */
	ProgramState shallowCopy();

	/**
	 * Creates a shallow copy in which the underlying heap is set to newHeap without creating a copy first.
 	 * @param newHeap The heap underlying the copy.
	 * @return A shallow copy with a reference to newHeap as underlying heap.
	 */
	ProgramState shallowCopyWithUpdateHeap( HeapConfiguration newHeap );

	/**
	 * Creates a shallow copy of this program state in which the underlying heap configuration is the same as in the
	 * copied object. The program counter of the copy is set to the provided new program counter value.
	 * @param newPC The value of the program counter of the copy.
	 * @return A shallow copy of this program state with the given program counter.
	 */
	ProgramState shallowCopyUpdatePC(int newPC);
}
