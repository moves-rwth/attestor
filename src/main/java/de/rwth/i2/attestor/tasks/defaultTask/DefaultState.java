package de.rwth.i2.attestor.tasks.defaultTask;


import de.rwth.i2.attestor.tasks.GeneralConcreteValue;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.tasks.GeneralProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.DebugMode;

/**
 * Simple implementation of program states for HRG-based analysis.
 *
 * @author Christoph
 */
public class DefaultState extends GeneralProgramState {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger( "DefaultState" );

    /**
     * The constants that should be present in every default state.
     */
	private static final String[] CONSTANT_NAMES = {"true", "false", "null", "1", "0"};

    /**
     * Initializes a program state with the default scope depth.
     * @param heap The underlying heap configuration.
     */
	public DefaultState( HeapConfiguration heap ) {
		super(heap);
	}

    /**
     * Initializes a program state with the default scope depth.
     * @param heap The underlying heap configuration.
     * @param scopeDepth The depth of the scope.
     */
	public DefaultState( HeapConfiguration heap, int scopeDepth ) {
		
		super( heap, scopeDepth);
	}

    /**
     * Creates a copy of the state.
     * @param state The state to be copied.
     */
	protected DefaultState(DefaultState state) {
		
		super( state );
	}

    /**
     * @return A hash code of this state.
     */
	public int hashCode() {

		return heap.hashCode();
	}

	@Override
	public ProgramState shallowCopy() {
		
		return new DefaultState(this);
	}

	@Override
	public ProgramState shallowCopyUpdatePC(int newPC) {
		
		DefaultState result = new DefaultState(this);
		result.setProgramCounter(newPC);
		return result;
		
	}

	@Override
	public GeneralConcreteValue getSelectorTarget(ConcreteValue from, String selectorName) {
		
		if(from instanceof GeneralConcreteValue) {
			
			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			
			if(dFrom.isUndefined()) {
				logger.warn("getSelectorTarget: origin is undefined");
				return dFrom;
			}
			
			GeneralSelectorLabel sel = GeneralSelectorLabel.getSelectorLabel(selectorName);
			
			int node = dFrom.getNode();
			node = heap.selectorTargetOf(node, sel);
			
			if(node == HeapConfiguration.INVALID_ELEMENT) {
				if(DebugMode.ENABLED) {
					logger.warn("getSelectorTarget got invalid value");
				}
				return GeneralConcreteValue.getUndefined();
			}
			
			Type type = heap.nodeTypeOf(node);
			
			return new GeneralConcreteValue( type, node );
		} else {
			
			if(DebugMode.ENABLED) {
				logger.warn("getSelectorTarget did not get a GeneralConcreteValue.");
			}
		}
		
		return GeneralConcreteValue.getUndefined();
	}

	@Override
	public void setSelector(ConcreteValue from, String selectorName, ConcreteValue to) {
		
		if(from.isUndefined() || to.isUndefined()) {
			
			logger.warn("Specified edge has invalid source or target.");
			return;
		}
		
		if(from instanceof GeneralConcreteValue && to instanceof GeneralConcreteValue) {

			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			GeneralConcreteValue dTo = (GeneralConcreteValue) to;
			
			GeneralSelectorLabel sel = GeneralSelectorLabel.getSelectorLabel(selectorName);
			
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
	public DefaultState clone() {
		
		HeapConfiguration newHeap = heap.clone();
		DefaultState result = new DefaultState(newHeap);
		result.setProgramCounter( programCounter );
		result.scopeDepth = scopeDepth;
		return result;
	}

	@Override
	public boolean equals(Object other) {

		if(other instanceof DefaultState) {

            DefaultState state = (DefaultState) other;

            return !(programCounter != state.programCounter || scopeDepth != state.scopeDepth)
                    && heap.equals(state.getHeap())
					&& atomicPropositions.equals(state.getAPs());

        }
		
		return false;
	}

	@Override
	protected String[] getConstants() {
		return CONSTANT_NAMES;
	}

}
