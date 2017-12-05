package de.rwth.i2.attestor.programState.defaultState;


import de.rwth.i2.attestor.grammar.inclusion.NormalFormInclusionStrategy;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.GeneralProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.ConcreteValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.GeneralConcreteValue;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
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
    private static final Logger logger = LogManager.getLogger("DefaultProgramState");

    private static HeapInclusionStrategy heapInclusionStrategy = new NormalFormInclusionStrategy();

    /**
     * Initializes a program state.
     *
     * @param heap The underlying heap configuration.
     */
    public DefaultProgramState(HeapConfiguration heap) {

        super(heap);
    }

    /**
     * Creates a copy of the state.
     *
     * @param state The state to be copied.
     */
    protected DefaultProgramState(DefaultProgramState state) {

        super(state);
    }

    public static void setHeapInclusionStrategy(HeapInclusionStrategy strategy) {

        heapInclusionStrategy = strategy;
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
    public GeneralConcreteValue getSelectorTarget(ConcreteValue from, SelectorLabel selectorLabel) {

        if (from != null && from.getClass() == GeneralConcreteValue.class) {

            GeneralConcreteValue dFrom = (GeneralConcreteValue) from;

            if (dFrom.isUndefined()) {
                logger.warn("getSelectorTarget: origin is undefined");
                return dFrom;
            }

            int baseNode = dFrom.getNode();
            Type baseNodeType = heap.nodeTypeOf(baseNode);
            if (!baseNodeType.hasSelectorLabel(selectorLabel)) {
                throw new IllegalStateException("Invalid selector '" + selectorLabel + "' for node of type '"
                        + baseNodeType + "'");
            }

            int node = heap.selectorTargetOf(baseNode, selectorLabel);

            if (node == HeapConfiguration.INVALID_ELEMENT) {

                if (baseNodeType.isPrimitiveType(selectorLabel)) {
                    return GeneralConcreteValue.getUndefined();
                } else {
                    throw new IllegalStateException("Required selector label " + from
                            + "." + selectorLabel + " is missing.");
                }
            }

            Type type = heap.nodeTypeOf(node);

            return new GeneralConcreteValue(type, node);
        } else {
            throw new IllegalStateException("getSelectorTarget did not get a GeneralConcreteValue.");
        }
    }


    @Override
    public void setSelector(ConcreteValue from, SelectorLabel selectorLabel, ConcreteValue to) {

        if (from.isUndefined() || to.isUndefined()) {
            logger.warn("Specified edge has undefined source or target.");
            return;
        }

        if (from.getClass() == GeneralConcreteValue.class && to.getClass() == GeneralConcreteValue.class) {
            GeneralConcreteValue dFrom = (GeneralConcreteValue) from;
            GeneralConcreteValue dTo = (GeneralConcreteValue) to;
            int fromNode = dFrom.getNode();
            Type fromType = heap.nodeTypeOf(fromNode);
            if(!fromType.hasSelectorLabel(selectorLabel)) {
               throw new IllegalStateException("Illegal request to set selector '" + selectorLabel
                       + "' for node of type '" + fromType + "'.");
            }
            try {
                if (heap.selectorTargetOf(fromNode, selectorLabel) != HeapConfiguration.INVALID_ELEMENT) {
                    heap.builder().removeSelector(fromNode, selectorLabel).build();
                }
                heap
                        .builder()
                        .addSelector(fromNode, selectorLabel, dTo.getNode())
                        .build();
            } catch (IllegalArgumentException e) {
                getHeap().builder().build();
                logger.warn("Specified edge has invalid source or target.");
            }
        }
    }

    @Override
    public DefaultProgramState clone() {

        HeapConfiguration newHeap = heap.clone();
        DefaultProgramState result = new DefaultProgramState(newHeap);
        result.setProgramCounter(programCounter);
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

        if (other == this) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (other.getClass() != DefaultProgramState.class) {
            return false;
        }

        DefaultProgramState state = (DefaultProgramState) other;
        HeapConfiguration otherHeap = state.getHeap();

        return programCounter == state.programCounter
                && heap.equals(otherHeap);
    }

    public boolean isSubsumedBy(ProgramState otherState) {

        return otherState == this
                || (otherState != null
                && programCounter == otherState.getProgramCounter()
                && heapInclusionStrategy.subsumes(heap, otherState.getHeap())
        );

    }
}
