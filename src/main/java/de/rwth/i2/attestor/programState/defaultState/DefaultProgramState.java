package de.rwth.i2.attestor.programState.defaultState;


import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.GeneralProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

/**
 * Simple implementation of program states for HRG-based analysis.
 *
 * @author Christoph
 */
public class DefaultProgramState extends GeneralProgramState {

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
    protected int getSelectorTargetOf(int sourceNode, SelectorLabel selectorLabel) {
        return heap.selectorTargetOf(sourceNode, selectorLabel);
    }

    @Override
    protected void removeSelector(int sourceNode, SelectorLabel selectorLabel) {
        if (heap.selectorTargetOf(sourceNode, selectorLabel) != HeapConfiguration.INVALID_ELEMENT) {
            heap.builder().removeSelector(sourceNode, selectorLabel).build();
        }
    }

    @Override
    protected SelectorLabel getNewSelector(SelectorLabel oldSelectorLabel) {
        return oldSelectorLabel;
    }

    @Override
    public DefaultProgramState clone() {

        HeapConfiguration newHeap = heap.clone();
        DefaultProgramState result = new DefaultProgramState(newHeap);
        result.setProgramCounter(programCounter);
        return result;
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
}
