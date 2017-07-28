package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.JimpleUtil;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * This class computes and stores the results of the abstract interpretation
 * of a method on given input heaps.
 * 
 * @author Hannah Arndt
 */
public class AbstractMethod {


	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "AbstractMethod" );
	
	/**
	 * stores all previously seen inputs with their fixpoints for reuse
	 */
	private final Map<HeapConfiguration, Set<ProgramState>> knownInputs;
	/**
	 * the abstract semantic of the method.
	 */
	private Program method;
	/**
	 * the methods signature
	 */
	private final String displayName;

	public AbstractMethod( String signature ){
		this(signature, signature);
	}

	public AbstractMethod( String signature, String displayName ){
		knownInputs = new HashMap<>();
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
	public void setControlFlow( List<Semantics> program ){
		this.method = new Program( program );
	}

	/**
	 * @return the method body / abstract semantics
	 */
	public Program getControlFlow(){
		return this.method;
	}

	/**
	 * @param input
	 *            the heap for which a result is needed
	 * @return true if the fixpoint for this input has already been calculated
	 */
	private boolean hasResult(HeapConfiguration input){
		return this.knownInputs.containsKey( input );
	}

	/**
	 * applies the abstract semantic of the method to the input until a fixpoint
	 * is reached.
	 * 
	 * @param input
	 *            the heap at the beginning of the method with all parameters
	 *            and if applicable this attached as intermediates to the
	 *            respective elements (i.e. nodes)
	 * @param scopeDepth 
	 * 			 The scope depth of the method to apply (necessary to distinguish
	 * 			 variables with identical name).
	 * @return all heaps which are in the fixpoint of the method at the terminal
	 *         states of it.
	 */
	public Set<ProgramState> getResult( HeapConfiguration input, int scopeDepth ){
		if( this.hasResult( input ) ){
			return knownInputs.get( input );
		}else{
			
			StateSpace stateSpace;
			Set<ProgramState> resultHeaps = new HashSet<>();
			stateSpace = JimpleUtil.getStateSpace(method, input, scopeDepth);
			resultHeaps.addAll(stateSpace.getFinalStates());
			
			return resultHeaps;
		}
	}

	/**
	 * sets the fixpoint result for the input heap in {@link #knownInputs}
	 * @param input the input heap
	 * @param results the set of resulting heaps
	 */
	public void setResult( HeapConfiguration input, Set<ProgramState> results ){
		this.knownInputs.put( input, results );
	}

	public String toString(){
		return this.displayName;
	}

}
