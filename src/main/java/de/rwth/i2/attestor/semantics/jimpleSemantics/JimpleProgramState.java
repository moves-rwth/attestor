
package de.rwth.i2.attestor.semantics.jimpleSemantics;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

/**
 * This interface captures all operations required to symbolically execute Jimple code.
 *
 * The semantics using these operations is found in the sub-package
 * {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple jimple}.
 *
 * To avoid collision between variables in different scopes, for example due to recursion,
 * the JimpleProgramState always remembers its current scope. If an operation on a variable is
 * performed, it will always reference the variable in the current scope and not affect variables
 * of the same name in another scope.
 * The user has to manage the scope. That is, she has to tell the JimpleProgramState whenever a scope is entered or
 * left.
 *
 * @author Hannah, Christoph
 */
public interface JimpleProgramState extends ProgramState {

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
     * @return A deep copy of this executable.
     */
	JimpleProgramState clone();
	
	/**
     * Provides the depth of the scope of this executable, which is necessary to pass this to abstract methods.
     *
	 * @return The current depth of the scope of this executable.
	 */
	int getScopeDepth();

    /**
     * Checks whether the given name corresponds to a constant (within scope) of this executable.
     * @param constant The name of the constant (without scope).
     * @return True if and only if the provided name (within scope) corresponds to a constant.
     */
	boolean isConstantName(String constant);
}
