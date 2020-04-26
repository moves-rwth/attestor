package de.rwth.i2.attestor.predicateAnalysis;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TAHeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.TransformationLog;
import de.rwth.i2.attestor.predicateAnalysis.relativeIndex.RelativeIndex;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.List;
import java.util.stream.Collectors;

public class PredicateAnalyzer {
    private AssignedMapping<RelativeIndex> assigned;
    private final Program program;
    private final StateSpace stateSpace;
    private final ExtremalValue<RelativeIndex> extremalValue;
    private final ForwardAbstractionRule<RelativeIndex> forwardAbstractionRule;
    private final BackwardAbstractionRule<RelativeIndex> backwardAbstractionRule;

    public PredicateAnalyzer(Program program, StateSpace stateSpace,
                             ExtremalValue<? extends RelativeIndex> extremalValue,
                             ForwardAbstractionRule<? extends RelativeIndex> forwardAbstractionRule,
                             BackwardAbstractionRule<? extends RelativeIndex> backwardAbstractionRule) {

        this.program = program;
        this.stateSpace = stateSpace;

        // TODO(mkh): replace with producer/consumer
        this.extremalValue = (ExtremalValue<RelativeIndex>) extremalValue;
        this.forwardAbstractionRule = (ForwardAbstractionRule<RelativeIndex>) forwardAbstractionRule;
        this.backwardAbstractionRule = (BackwardAbstractionRule<RelativeIndex>) backwardAbstractionRule;
    }

    public void start() {
        List<ProgramState> criticalStates = stateSpace
                .getStates()
                .stream()
                .filter(state -> program.getStatement(state.getProgramCounter()) instanceof GotoStmt)
                .collect(Collectors.toList());

/*
        List<SemanticsCommand> statements = stateSpace
                .getStates()
                .stream()
                .map(state -> program.getStatement(state.getProgramCounter()))
                .collect(Collectors.toList());
*/

        criticalStates.forEach(state -> {
            HeapConfiguration initialHeap = state.getHeap();

            TIntObjectMap<RelativeIndex> initialFragment = new TIntObjectHashMap<>();
            initialHeap.nonterminalEdges().forEach(edge -> {
                initialFragment.put(edge, extremalValue.ofNonTerminal(initialHeap.labelOf(edge)));
                return true;
            });

            assigned = new AssignedMapping<>(initialFragment);
            analyzeState(state, 0);
        });
    }

    private void analyzeState(ProgramState state, int key) {
        if (!(state.getHeap() instanceof TAHeapConfiguration)) {
            throw new IllegalArgumentException("only transformation-aware heap configurations can be analyzed");
        }

        if (stateSpace.getFinalStates().contains(state)) {
            return;
        }

        stateSpace.getMaterializationSuccessorsOf(state).forEach(successor -> {
            TAHeapConfiguration concreteHeap = (TAHeapConfiguration) successor.getHeap();

            int newKey = materializationTransferStep(key, concreteHeap);
            analyzeState(successor, newKey);
        });

        stateSpace.getControlFlowSuccessorsOf(state).forEach(successor -> {
            TAHeapConfiguration concreteHeap = (TAHeapConfiguration) successor.getHeap();

            int newKey = abstractionTransferStep(key, concreteHeap);
            analyzeState(successor, newKey);
        });
    }

    private int materializationTransferStep(int parentKey, TAHeapConfiguration heapConf) {
        TransformationLog transformationLog = heapConf.getTransformationLog();
        RelativeIndex replacedIndex = assigned.get(parentKey, transformationLog.ntEdge);
        TIntObjectMap<RelativeIndex> updateFragment = new TIntObjectHashMap<>();

        updateFragment.put(transformationLog.ntEdge, null);
        TIntObjectMap<RelativeIndex> result = forwardAbstractionRule.apply(replacedIndex, transformationLog.rule);
        result.forEachEntry((id, index) -> {
            updateFragment.put(transformationLog.match(id), index);
            return false;
        });

        return assigned.update(parentKey, updateFragment);
    }


    private int abstractionTransferStep(int parentKey, TAHeapConfiguration heapConf) {
        TransformationLog transformationLog = heapConf.getTransformationLog();
        TIntObjectMap<RelativeIndex> updateFragment = new TIntObjectHashMap<>();

        if (transformationLog != null) {  // no canonicalization

            TIntObjectMap<RelativeIndex> assign = new TIntObjectHashMap<>();
            transformationLog.rule.nonterminalEdges().forEach(edge -> {
                int match = transformationLog.match(edge);
                assign.put(match, assigned.get(parentKey, edge));
                updateFragment.put(match, null);
                return true;
            });
            RelativeIndex result = backwardAbstractionRule.apply(assign, transformationLog.rule);
            updateFragment.put(transformationLog.ntEdge, result);
        }

        return assigned.update(parentKey, updateFragment);
    }
}
