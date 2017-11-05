package de.rwth.i2.attestor.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.types.Type;

/**
 * An abstraction of a single state in a StateSpace.
 * This comprises information about the current program location, that is the value of the program counter,
 * of the state and the program's heap.
 *
 * @author Christoph, Hannah
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
	 * Manages the scope in order to distinguish variables of the same name, for example within recursion.
	 * This method must be called before the fixpoint for the invoked method is computed.
	 */
	void enterScope();

	/**
	 * Manages the scope in order to distinguish variables of the same name, for example within recursion.
	 *
	 * This method must be called before any value in the calling method is
	 * assigned (for example in x = foo(), decrease must be called after evaluating
	 * foo() but before the return value is attached to x ).
	 */
	void leaveScope();

	/**
	 * Gets the target of the variable in the current scope.
	 *
	 * @param variableName The name of the requested variable (without scope).
	 * @return The referenced element on the heap.
	 */
	ConcreteValue getVariableTarget(String variableName);

	/**
	 * Removes the variable (in the current scope) from the executable.
	 *
	 * @param variableName The name of the variable to remove (without scope).
	 */
	void removeVariable(String variableName);

	/**
	 * Sets the variable of the given name in the current scope to the given value.
	 * Resets the variable if it has been there previously.
	 *
	 * @param variableName the name of the variable (without scope)
	 * @param value the value (i.e. node) to which the variable should be set
	 */
	void setVariable(String variableName, ConcreteValue value);

	/**
	 * Gets the requested constant - constants are global, i.e. they don't have a scope.
	 * @param constantName The name of the requested constant.
	 * @return The element in the heap that is referenced by the constant.
	 */
	ConcreteValue getConstant(String constantName);

	/**
	 * Removes an intermediate (in the current scope) from the executable.
	 * Intermediates are internal variables for communication between methods, such that this, return,
	 * param_n, etc.
	 *
	 * @param name The name of the intermediate (without scope).
	 * @return The ConcreteValue that is referenced by the intermediate.
	 */
	ConcreteValue removeIntermediate(String name);

	/**
     * Sets an intermediate (in the current scope) to the given value.
     * Intermediates are internal variables for communication between methods, such that this, return,
     * param_n, etc.
	 *
	 * @param name The name of the intermediate (without scope).
	 * @param value The ConcreteValue that will should be referenced by the intermediate.
	 */
	void setIntermediate(String name, ConcreteValue value);

	/**
	 * Gets the target of the given selector starting at a given object on the heap.
	 *
	 * @param from The element on the heap whose selectors should be considered.
	 * @param selectorName The name of the selector.
	 * @return The element on the heap that is the target of the selector.
	 */
	ConcreteValue getSelectorTarget(ConcreteValue from, String selectorName);

	/**
	 * (Re)sets the given selector starting at the given object on the heap to a (new) target.
	 *
	 * @param from The element on the heap whose selectors should be considered.
	 * @param selectorName The name of the selector.
	 * @param to The element on the heap that should be the target of the selector.
	 */
	void setSelector(ConcreteValue from, String selectorName, ConcreteValue to);

	/**
	 * Adds a new element on the heap to the executable and returns it.
	 *
	 * @param type The type of the new element.
	 * @return The added element.
	 */
	ConcreteValue insertNewElement(Type type);

	/**
	 * Gets an undefined value.
	 *
	 * @return The concrete value that stands for undefined.
	 */
	ConcreteValue getUndefined();

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

	ProgramState clone();

	/**
	 * Provides the depth of the scope of this executable, which is necessary to pass this to abstract methods.
	 *
	 * @return The current depth of the scope of this executable.
	 */
	int getScopeDepth();
}
