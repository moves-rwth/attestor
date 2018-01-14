package de.rwth.i2.attestor.markingGeneration.neighbourhood;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.AbstractMarkingGenerator;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class NeighbourhoodMarkingGenerator extends AbstractMarkingGenerator {

    private final String initialMarkingName;
    private final String markingName;

    public NeighbourhoodMarkingGenerator(String initialMarkingName, String markingName,
                                         Collection<String> availableSelectorLabelNames,
                                         AbortStrategy abortStrategy,
                                         MaterializationStrategy materializationStrategy,
                                         CanonicalizationStrategy canonicalizationStrategy,
                                         CanonicalizationStrategy aggressiveCanonicalizationStrategy) {

        super(availableSelectorLabelNames, abortStrategy, materializationStrategy,
                canonicalizationStrategy, aggressiveCanonicalizationStrategy);

        this.initialMarkingName = initialMarkingName;
        this.markingName = markingName;
    }

    @Override
    protected List<ProgramState> placeInitialMarkings(ProgramState initialState) {

        List<ProgramState> result = new LinkedList<>();
        HeapConfiguration initialHeap = initialState.getHeap();
        TIntIterator iterator = initialHeap.nodes().iterator();
        while(iterator.hasNext()) {
            int node = iterator.next();
            if(!isAttachedToConstant(initialHeap, node)) {
                HeapConfiguration markedHeap = initialHeap.clone()
                        .builder()
                        .addVariableEdge(initialMarkingName, node)
                        .build();
                ProgramState markedState = initialState.shallowCopyWithUpdateHeap(markedHeap);
                markedState.setProgramCounter(0);
                result.add(markedState);
            }
        }
        return result;
    }

    private boolean isAttachedToConstant(HeapConfiguration heap, int node) {

        TIntArrayList attachedVariables = heap.attachedVariablesOf(node);
        for(int i=0; i< attachedVariables.size(); i++) {
            int var = attachedVariables.get(i);
            if(Constants.isConstant(heap.nameOf(var))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected Program getProgram() {

        SemanticsCommand command = new NeighbourhoodMarkingCommand(0,
                initialMarkingName, markingName, "-",
                getAvailableSelectorLabelNames()
        );

        return new ProgramImpl(Collections.singletonList(command));
    }

    @Override
    protected boolean isResultState(StateSpace stateSpace, ProgramState programState) {

        return programState.getHeap().variableWith(initialMarkingName) != HeapConfiguration.INVALID_ELEMENT;
    }
}
