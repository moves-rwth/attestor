package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import java.util.List;
import java.util.Set;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

public abstract class AbstractMethod {

	public interface StateSpaceFactory {


		StateSpace create(Program method, HeapConfiguration input, int scopeDepth) throws StateSpaceGenerationAbortedException;
	}

	/**
	 * the abstract semantic of the method.
	 */
	protected Program method;

	/**
	 * Provides the results of symbolically executing the method represented by this object
	 * on the given input.
	 * @param input The heap configuration determining the input of the method.
	 * @param scopeDepth The current scope of the call stack.
	 * @param options The current state space generation options.
	 * @return The state space obtained from symbolically executing this AbstractMethod on the
	 *         given input.
	 * @throws StateSpaceGenerationAbortedException
	 */
	public abstract Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth, SemanticsOptions options)
			throws StateSpaceGenerationAbortedException;

	/**
	 * the methods signature
	 */
	protected final String displayName;

	public AbstractMethod( String displayName ) {
		super();
		this.displayName = displayName;
	}

	/**
	 * sets the methods semantic to the control flow of the given list of
	 * abstract semantics
	 * 
	 * @param program
	 *            a list of abstract semantics which are the translation of the
	 *            method body
	 */
	public void setControlFlow(List<Semantics> program) {
		this.method = new Program( program );
	}

	/**
	 * @return the method body / abstract semantics
	 */
	public Program getControlFlow() {
		return this.method;
	}

	public String toString(){
		return this.displayName;
	}

}