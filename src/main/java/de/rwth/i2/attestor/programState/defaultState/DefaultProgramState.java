package de.rwth.i2.attestor.programState.defaultState;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.grammar.inclusion.NormalFormInclusionStrategy;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.GeneralProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.types.Type;

/**
 * Simple implementation of program states for HRG-based analysis.
 *
 * @author Christoph
 */
public class DefaultProgramState extends GeneralProgramState {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger( "DefaultProgramState" );

	private static HeapInclusionStrategy heapInclusionStrategy = new NormalFormInclusionStrategy();

	public static void setHeapInclusionStrategy(HeapInclusionStrategy strategy) {
		heapInclusionStrategy = strategy;
	}

	/**
     * Initializes a program state.
     * @param heap The underlying heap configuration.
     */
	public DefaultProgramState(HeapConfiguration heap ) {
		
		super( heap );
	}

    /**
     * Creates a copy of the state.
     * @param state The state to be copied.
     */
	protected DefaultProgramState(DefaultProgramState state) {
		
		super( state );
	}

	@Override
	public ProgramState shallowCopy() {
		
		return new DefaultProgramState(this);
	}

	@Override
	public ProgramState shallowCopyUpdatePC(int newPC) {
		
		DefaultProgramState result = new DefaultProgramState(this);
		result.setProgramCounter(newPC);
		return result;
		
	}

	@Override
	public GeneralConcreteValue getSelectorTarget(ConcreteValue from, String selectorName) {
		
		if(from != null && from.getClass() == GeneralConcreteValue.class) {

			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			
			if(dFrom.isUndefined()) {
				logger.warn("getSelectorTarget: origin is undefined");
				return dFrom;
			}
			
			BasicSelectorLabel sel = BasicSelectorLabel.getSelectorLabel(selectorName);
			
			int node = dFrom.getNode();
			node = heap.selectorTargetOf(node, sel);
			
			if(node == HeapConfiguration.INVALID_ELEMENT) {
				// this is not an error, because assignments to fields might first check whether
				// a selector exists.
				return GeneralConcreteValue.getUndefined();
			}
			
			Type type = heap.nodeTypeOf(node);
			
			return new GeneralConcreteValue( type, node );
		} else {
			logger.warn("getSelectorTarget did not get a GeneralConcreteValue.");
		}
		
		return GeneralConcreteValue.getUndefined();
	}

	@Override
	public void setSelector(ConcreteValue from, String selectorName, ConcreteValue to) {
		
		if(from.isUndefined() || to.isUndefined()) {
			
			logger.warn("Specified edge has undefined source or target.");
			return;
		}
		
		if(from.getClass() == GeneralConcreteValue.class && to.getClass() == GeneralConcreteValue.class) {

			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			GeneralConcreteValue dTo = (GeneralConcreteValue) to;
			
			BasicSelectorLabel sel = BasicSelectorLabel.getSelectorLabel(selectorName);
			
			int fromNode = dFrom.getNode();
			
			try {
				
				if(heap.selectorTargetOf(fromNode, sel) != HeapConfiguration.INVALID_ELEMENT) {
					heap.builder().removeSelector(fromNode, sel).build();
				}
				
				heap
				.builder()
				.addSelector(fromNode, sel, dTo.getNode())
				.build();
			} catch(IllegalArgumentException e) {
				
				getHeap().builder().build();
				logger.warn("Specified edge has invalid source or target.");
            }
			
		}
	}

	@Override	
	public DefaultProgramState clone() {
		
		HeapConfiguration newHeap = heap.clone();
		DefaultProgramState result = new DefaultProgramState(newHeap);
		result.setProgramCounter( programCounter );
		return result;
	}

	@Override
    public int hashCode() {

	    int hash = programCounter;
	    hash = (hash << 1) ^ heap.hashCode();
	    return hash;
    }

	@Override
	public boolean equals(Object other) {

		if(other == this) {
			return true;
		}

		if(other == null) {
			return false;
		}

		if(other.getClass() != DefaultProgramState.class) {
			return false;
		}

        DefaultProgramState state = (DefaultProgramState) other;
		HeapConfiguration otherHeap = state.getHeap();

        return programCounter == state.programCounter
					&& heap.equals(otherHeap);
	}

	public boolean isSubsumedBy(ProgramState otherState) {

		if(otherState == this) {
			return true;
		}

		if(otherState == null) {
			return false;
		}

		return programCounter == otherState.getProgramCounter()
				&& heapInclusionStrategy.subsumes(heap, otherState.getHeap());
	}
}