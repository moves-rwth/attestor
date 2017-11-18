package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.List;
import java.util.Set;

public abstract class AbstractMethod {

	/**
	 * the abstract semantic of the method.
	 */
	protected Program method;

	private boolean reuseResultsEnabled = true;

	public void setReuseResults(boolean enabled) {
		this.reuseResultsEnabled = enabled;
	}

	public boolean isReuseResultsEnabled() {
		return reuseResultsEnabled;
	}

	/**
	 * Provides the results of symbolically executing the method represented by this object
	 * on the given input.
	 * @param input The program state determining the input of the method.
	 * @param options The current state space generation options.
	 * @return The state space obtained from symbolically executing this AbstractMethod on the
	 *         given input.
	 * @throws StateSpaceGenerationAbortedException
	 */
	public abstract Set<ProgramState> getResult(ProgramState input, SymbolicExecutionObserver options)
			throws StateSpaceGenerationAbortedException;

	/**
	 * the methods signature
	 */
	protected String displayName;

	public AbstractMethod( ) {
		super();
	}
	
	public void setDisplayName( String displayName ){
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

	public abstract Set<ProgramState> getFinalStates(ProgramState input, SymbolicExecutionObserver observer);

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