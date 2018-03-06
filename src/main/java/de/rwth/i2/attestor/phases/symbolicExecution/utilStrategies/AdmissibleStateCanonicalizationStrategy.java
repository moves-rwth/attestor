package de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies;

import de.rwth.i2.attestor.grammar.canonicalization.CanonicalizationStrategy;
import de.rwth.i2.attestor.grammar.materialization.strategies.MaterializationStrategy;
import de.rwth.i2.attestor.grammar.materialization.util.ViolationPoints;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markingGeneration.Markings;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateCanonicalizationStrategy;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import gnu.trove.iterator.TIntIterator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class AdmissibleStateCanonicalizationStrategy implements StateCanonicalizationStrategy {

    private final CanonicalizationStrategy canonicalizationStrategy;
    private final MaterializationStrategy materializationStrategy;

    public AdmissibleStateCanonicalizationStrategy(
        CanonicalizationStrategy canonicalizationStrategy,
        MaterializationStrategy materializationStrategy
    ) {

        this.canonicalizationStrategy = canonicalizationStrategy;
        this.materializationStrategy = materializationStrategy;
    }

    @Override
    public Collection<ProgramState> canonicalize(ProgramState state) {

        HeapConfiguration abstractHeap = canonicalizationStrategy.canonicalize(state.getHeap());

        ViolationPoints violationPoints = computeViolationPoints(abstractHeap);
        Collection<HeapConfiguration> admissibleHeaps = materializationStrategy
                .materialize(abstractHeap, violationPoints);

        if(admissibleHeaps.isEmpty()) {
            admissibleHeaps = Collections.singleton(abstractHeap);
        }

        Collection<ProgramState> result = new ArrayList<>(admissibleHeaps.size());
        for(HeapConfiguration heap : admissibleHeaps) {
            result.add(state.shallowCopyWithUpdateHeap(heap));
        }
        return result;
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
