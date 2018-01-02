package de.rwth.i2.attestor.markingGeneration;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.stateSpaceGeneration.AbortStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.ProgramImpl;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VisitedMarkingGenerator extends AbstractMarkingGenerator {


    public VisitedMarkingGenerator(Collection<String> availableSelectorLabelNames,
                                   AbortStrategy abortStrategy,
                                   MaterializationStrategy materializationStrategy,
                                   CanonicalizationStrategy canonicalizationStrategy) {

        super(availableSelectorLabelNames, abortStrategy, materializationStrategy, canonicalizationStrategy);
    }

    @Override
    protected List<ProgramState> placeInitialMarkings(ProgramState initialState) {

        List<ProgramState> result = new LinkedList<>();
        HeapConfiguration initialHeap = initialState.getHeap();
        TIntIterator iterator = initialHeap.nodes().iterator();
        while(iterator.hasNext()) {
            int node = iterator.next();
            Type type = initialHeap.nodeTypeOf(node);
            if(!Types.isConstantType(type)) {
                HeapConfiguration markedHeap = initialHeap.clone()
                        .builder()
                        .addVariableEdge(VisitedMarkingCommand.MARKING_NAME, node)
                        .build();
                ProgramState markedState = initialState.shallowCopyWithUpdateHeap(markedHeap);
                markedState.setProgramCounter(0);
                result.add(markedState);
            }
        }
        return result;
    }

    @Override
    protected Program getProgram() {

        SemanticsCommand command = new VisitedMarkingCommand(getAvailableSelectorLabelNames(), 0);
        return new ProgramImpl(Collections.singletonList(command));
    }
}
