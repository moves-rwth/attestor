package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * This class computes and stores the results of the abstract interpretation
 * of a method on given input heaps.
 * 
 * @author Hannah Arndt
 */
public class SimpleAbstractMethod extends AbstractMethod {

	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "AbstractMethod" );
	
	/**
	 * stores all previously seen inputs with their fixpoints for reuse
	 */
	private final Map<HeapConfiguration, Set<ProgramState>> knownInputs;

	public SimpleAbstractMethod( String signature){
		this(signature, signature);
	}

	public SimpleAbstractMethod( String signature, String displayName){
		super( displayName  );
		knownInputs = new HashMap<>();
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
	 * @param options
	 * 			 The current options for the symbolic execution.
	 * @return all heaps which are in the fixpoint of the method at the terminal
	 *         states of it.
	 */
	@Override
	public Set<ProgramState> getResult( HeapConfiguration input, int scopeDepth, SemanticsOptions options )
		throws StateSpaceGenerationAbortedException {
		if( this.hasResult( input ) ){
			return knownInputs.get( input );
		}else{
			
			Set<ProgramState> resultHeaps = new HashSet<>();

			StateSpace stateSpace = options.generateStateSpace(method, input, scopeDepth);
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

}
