package de.rwth.i2.attestor.grammar.admissibility;

import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateMaterializationStrategy;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.Collection;
import java.util.Collections;

public class AdmissibilityStrategy {

    private final StateMaterializationStrategy materializationStrategy;

    public AdmissibilityStrategy(StateMaterializationStrategy materializationStrategy) {

        this.materializationStrategy = materializationStrategy;
    }

    public Collection<ProgramState> getAdmissibleStatesOf(ProgramState state) {

        HeapConfiguration hc = state.getHeap();
        TIntIterator variableIter = hc.variableEdges().iterator();
        ViolationPoints violationPoints = new ViolationPoints();

        while(variableIter.hasNext()) {

            int var = variableIter.next();
            String label = hc.nameOf(var);

            if(Markings.isMarking(label) || Constants.isConstant(label)) {
                continue;
            }

            int target = hc.targetOf(var);
            Type targetType = hc.nodeTypeOf(target);

            if(Types.isConstantType(targetType)) {
                continue;
            }

            for(SelectorLabel sel: targetType.getSelectorLabels().keySet()) {
                violationPoints.add(label, sel.getLabel());
            }
        }

        Collection<ProgramState> result = materializationStrategy.materialize(state, violationPoints);
        if(result.isEmpty()) {
            return Collections.singleton(state);
        } else {
            return result;
        }


    }
}
