package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.GeneralProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;

public class IndexedState extends GeneralProgramState {

    public IndexedState(HeapConfiguration heap) {
        super(heap);
    }

    private IndexedState(IndexedState state) {

        super(state);
    }

    @Override
    public ProgramState shallowCopy() {

        return new IndexedState(this);
    }

    @Override
    public boolean equals(Object other) {

        if (other == this) {
            return true;
        }

        if (other == null) {
            return false;
        }

        if (other.getClass() != IndexedState.class) {
            return false;
        }

        IndexedState state = (IndexedState) other;

        return programCounter == state.programCounter
                && heap.equals(state.getHeap());
    }

    @Override
    protected int getSelectorTargetOf(int sourceNode, SelectorLabel selectorLabel) {
        String selectorName = selectorLabel.getLabel();
        for (SelectorLabel label : getHeap().selectorLabelsOf(sourceNode)) {
            AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) label;
            if (sel.hasLabel(selectorName)) {
                return getHeap().selectorTargetOf(sourceNode, sel);
            }
        }
        return HeapConfiguration.INVALID_ELEMENT;
    }

    @Override
    protected void removeSelector(int sourceNode, SelectorLabel selectorLabel) {

        String selectorName = selectorLabel.getLabel();
        for (SelectorLabel label : getHeap().selectorLabelsOf(sourceNode)) {

            AnnotatedSelectorLabel sel = (AnnotatedSelectorLabel) label;
            if (sel.hasLabel(selectorName)) {
                this.getHeap()
                        .builder()
                        .removeSelector(sourceNode, sel)
                        .build();
            }
        }

    }

    @Override
    protected SelectorLabel getNewSelector(SelectorLabel oldSelectorLabel) {
        return new AnnotatedSelectorLabel(oldSelectorLabel);
    }

    @Override
    public ProgramState shallowCopyUpdatePC(int newPC) {

        IndexedState result = new IndexedState(this);
        result.setProgramCounter(newPC);
        return result;
    }

    @Override
    public IndexedState clone() {

        HeapConfiguration newHeap = heap.clone();
        IndexedState result = new IndexedState(newHeap);
        result.setProgramCounter(programCounter);
        return result;
    }
}
