package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateMaterializationStrategy;
import de.rwth.i2.attestor.stateSpaceGeneration.StateRectificationStrategy;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.Collection;
import java.util.Collections;

public class AdmissibleStateRectificationStrategy implements StateRectificationStrategy {

    private final StateMaterializationStrategy materializationStrategy;

    public AdmissibleStateRectificationStrategy(StateMaterializationStrategy materializationStrategy) {

        this.materializationStrategy = materializationStrategy;
    }

    @Override
    public Collection<ProgramState> rectify(ProgramState state) {

        HeapConfiguration heapConfiguration = state.getHeap();
        ViolationPoints violationPoints = computeViolationPoints(heapConfiguration);
        Collection<ProgramState> admissibleStates = materializationStrategy.materialize(state, violationPoints);

        if(admissibleStates.isEmpty()) {
            admissibleStates = Collections.singleton(state);
        }

        return admissibleStates;


    }

    private ViolationPoints computeViolationPoints(HeapConfiguration heap) {

        ViolationPoints result = new ViolationPoints();
        TIntIterator variableIterator = heap.variableEdges().iterator();

        while(variableIterator.hasNext()) {

            int var = variableIterator.next();
            String label = heap.nameOf(var);

            if(Markings.isMarking(label) || Constants.isConstant(label)) {
                continue;
            }

            int target = heap.targetOf(var);
            Type targetType = heap.nodeTypeOf(target);

            if(Types.isConstantType(targetType)) {
                continue;
            }

            for(SelectorLabel sel: targetType.getSelectorLabels().keySet()) {
                result.add(label, sel.getLabel());
            }
        }

        return result;
    }

}
