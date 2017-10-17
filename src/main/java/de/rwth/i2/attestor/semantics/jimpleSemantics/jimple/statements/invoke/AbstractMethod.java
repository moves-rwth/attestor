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

	public abstract Set<ProgramState> getResult(HeapConfiguration input, int scopeDepth) throws StateSpaceGenerationAbortedException;

	/**
	 * the methods signature
	 */
	protected final String displayName;
	/**
	 * Factory to obtain a state space.
	 */
	protected StateSpaceFactory factory;

	public AbstractMethod( String displayName, StateSpaceFactory factory ) {
		super();
		this.displayName = displayName;
		this.factory = factory;
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