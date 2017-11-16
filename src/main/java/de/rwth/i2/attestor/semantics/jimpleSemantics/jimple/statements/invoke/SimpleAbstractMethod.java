package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsObserver;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private final Map<HeapConfiguration, Set<ProgramState>> knownInputs = new HashMap<>();

	public SimpleAbstractMethod( String signature){
		this(signature, signature);
	}

	@Override
	public Set<ProgramState> getFinalStates(ProgramState input, SemanticsObserver observer) {

		return knownInputs.get(input.getHeap());
	}

	public SimpleAbstractMethod( String signature, String displayName){
		super( );
		super.setDisplayName(displayName);
	}

	/**
	 * @param input
	 *            the heap for which a result is needed
	 * @return true if the fixpoint for this input has already been calculated
	 */
	private boolean hasResult(HeapConfiguration input){
		return this.knownInputs.containsKey( input );
	}

	@Override
	public Set<ProgramState> getResult(ProgramState input, SemanticsObserver observer )
		throws StateSpaceGenerationAbortedException {

		observer.update(this, input);

		HeapConfiguration heap = input.getHeap();
		if( isReuseResultsEnabled() && this.hasResult( heap ) ){
			return knownInputs.get( heap );
		}else{

			Set<ProgramState> resultHeaps = new HashSet<>();

			StateSpace stateSpace = observer.generateStateSpace(method, input);
			resultHeaps.addAll(stateSpace.getFinalStates());
			setResult(heap, resultHeaps);

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
