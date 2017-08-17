package de.rwth.i2.attestor.strategies.defaultGrammarStrategies;


import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.strategies.GeneralJimpleProgramState;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.DebugMode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;

/**
 * Simple implementation of program states for HRG-based analysis.
 *
 * @author Christoph
 */
public class DefaultProgramState extends GeneralJimpleProgramState {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger( "DefaultProgramState" );

    /**
     * The constants that should be present in every default state.
     */
	private static final String[] CONSTANT_NAMES = {"true", "false", "null", "1", "0"};

    /**
     * Initializes a program state with the default scope depth.
     * @param heap The underlying heap configuration.
     */
	public DefaultProgramState(HeapConfiguration heap ) {
		super(heap);
	}

    /**
     * Initializes a program state with the default scope depth.
     * @param heap The underlying heap configuration.
     * @param scopeDepth The depth of the scope.
     */
	public DefaultProgramState(HeapConfiguration heap, int scopeDepth ) {
		
		super( heap, scopeDepth);
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
	public DefaultProgramState clone() {
		
		HeapConfiguration newHeap = heap.clone();
		DefaultProgramState result = new DefaultProgramState(newHeap);
		result.setProgramCounter( programCounter );
		result.scopeDepth = scopeDepth;
		return result;
	}

	@Override
    public int hashCode() {

	    return Objects.hash(heap, programCounter, scopeDepth);
    }

	@Override
	public boolean equals(Object other) {

		if(other instanceof DefaultProgramState) {

            DefaultProgramState state = (DefaultProgramState) other;

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
