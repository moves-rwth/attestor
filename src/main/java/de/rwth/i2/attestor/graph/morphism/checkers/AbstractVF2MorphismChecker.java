package de.rwth.i2.attestor.graph.morphism.checkers;

import java.util.List;

import de.rwth.i2.attestor.graph.morphism.Graph;
import de.rwth.i2.attestor.graph.morphism.Morphism;
import de.rwth.i2.attestor.graph.morphism.MorphismChecker;
import de.rwth.i2.attestor.graph.morphism.VF2Algorithm;

/**
 * An abstract class providing a default implementation of {@link MorphismChecker}
 * to find graph morphisms mapping a pattern graph into a target graph.
 * 
 * Subclasses of AbstractVF2MorphismChecker usually determine the actual algorithm
 * that is applied to find morphisms.
 * 
 * The class supports to check whether at least one morphism exists and to iteratively
 * get all existing morphisms.
 * 
 * @author Christoph
 *
 */
public abstract class AbstractVF2MorphismChecker implements MorphismChecker {
	
	/**
	 * Stores whether at least one morphism could be found.
	 */
	private boolean hasMorphism;
	
	/**
	 * A list of all morphisms that could be found.
	 */
	private List<Morphism> foundMorphisms;
	
	/**
	 * The algorithm that is used to determine graph morphisms.
	 */
	private final VF2Algorithm matchingAlgorithm;
	
	/**
	 * Initializes this checker.
	 * 
	 * @param matchingAlgorithm The algorithm to determine graph morphisms.
	 */
    AbstractVF2MorphismChecker(VF2Algorithm matchingAlgorithm) {
		
		this.matchingAlgorithm = matchingAlgorithm;
	}
	
	/**
	 * Starts searching for graph morphisms of the pattern graph into the target graph.
	 * 
	 * @param pattern The pattern graph.
	 * @param target The target graph.
	 */
	public void run(Graph pattern, Graph target) {
		
		hasMorphism = matchingAlgorithm.match(pattern, target);
		foundMorphisms = matchingAlgorithm.getFoundMorphisms();
	}
	
	@Override
	public boolean hasMorphism() {
		return hasMorphism;
	}

	@Override
	public boolean hasNext() {	
		return foundMorphisms != null && !foundMorphisms.isEmpty();
	}

	@Override
	public Morphism getNext() {
		
		if(hasNext()) {
			return foundMorphisms.remove(0);
		}
		
		throw new IndexOutOfBoundsException("no further morphism exists");
		
	}
}
