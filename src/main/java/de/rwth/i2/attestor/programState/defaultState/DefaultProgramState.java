package de.rwth.i2.attestor.programState.defaultState;


import de.rwth.i2.attestor.grammar.inclusion.NormalFormInclusionStrategy;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.semantics.util.PrimitiveTypes;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.programState.GeneralProgramState;
import de.rwth.i2.attestor.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		
		if(from != null && from.getClass() == GeneralConcreteValue.class) {

			GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
			
			if(dFrom.isUndefined()) {
				logger.warn("getSelectorTarget: origin is undefined");
				return dFrom;
			}
			
			BasicSelectorLabel sel = BasicSelectorLabel.getSelectorLabel(selectorName);
			
			int baseNode = dFrom.getNode();
            Type baseNodeType = heap.nodeTypeOf(baseNode);
            if(!baseNodeType.hasSelectorLabel(selectorName)) {
                throw new IllegalStateException("Invalid selector '" + selectorName + "' for node of type '"
                    + baseNodeType + "'");
            }

			int node = heap.selectorTargetOf(baseNode, sel);
			
			if(node == HeapConfiguration.INVALID_ELEMENT) {

			    if(baseNodeType.isPrimitiveType(selectorName)) {
                    return GeneralConcreteValue.getUndefined();
				} else {
                    return GeneralConcreteValue.getUndefined();
					//throw new IllegalStateException("Required selector label " + from + "." + sel + " is missing.");
				}
			}
			
			Type type = heap.nodeTypeOf(node);
			
			return new GeneralConcreteValue( type, node );
		} else {
			throw new IllegalStateException("getSelectorTarget did not get a GeneralConcreteValue.");
		}
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
		result.scopeDepth = scopeDepth;
		return result;
	}

	@Override
    public int hashCode() {

	    int hash = programCounter;
	    hash = (hash << 1) ^ scopeDepth;
	    hash = (hash << 1) ^ heap.hashCode();
	    return hash;
    }

    @Override
    protected SelectorLabel getSelectorLabel(String selectorLabelName) {
	    return BasicSelectorLabel.getSelectorLabel(selectorLabelName);
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
					&& scopeDepth == state.scopeDepth
					&& heap.equals(otherHeap);
	}

	public boolean isSubsumedBy(ProgramState otherState) {

		if (otherState == this) {
			return true;
		}

		return otherState != null
				&& programCounter == otherState.getProgramCounter()
				&& scopeDepth == otherState.getScopeDepth()
				&& heapInclusionStrategy.subsumes(heap, otherState.getHeap());

	}
}
