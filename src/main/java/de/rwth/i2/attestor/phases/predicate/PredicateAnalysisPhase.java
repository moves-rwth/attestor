package de.rwth.i2.attestor.phases.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.WorklistAlgorithm;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.transformers.AbstractionRuleTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.predicateAnalysis.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.util.Pair;

import java.util.*;
import java.util.stream.Collectors;

public class PredicateAnalysisPhase extends AbstractPhase {
    private final Map<Integer, Set<Integer>> terminationResults = new HashMap<>();
    private final Map<
            Pair<Integer, Integer>,
            Map<Integer, AssignMapping<RelativeInteger>>> dataFlowResults = new HashMap<>();

    public PredicateAnalysisPhase(Scene scene) {
        super(scene);
    }

    @Override
    public String getName() {
        return "Predicate Analysis";
    }

    @Override
    public void executePhase() {
        if (!scene().options().isPredicateMode()) {
            return;
        }

        Program program = getPhase(ProgramTransformer.class).getProgram();
        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        StateSpaceAdapter adapter = new StateSpaceAdapter(stateSpace, program, Collections.singleton(GotoStmt.class));

        //noinspection unchecked
        AbstractionRule<RelativeInteger> abstractionRule = getPhase(AbstractionRuleTransformer.class).getAbstractionRule();
        EquationSolver<AssignMapping<RelativeInteger>> solver = new WorklistAlgorithm<>();

        for (int critical : adapter.getCriticalLabels()) {
            PredicateAnalysis<RelativeInteger> analysis =
                    new PredicateAnalysis<>(critical, adapter, RelativeInteger.opSet, abstractionRule, RelativeInteger.get(30));

            dataFlowResults.put(new Pair<>(critical, analysis.getUntangled()), solver.solve(analysis));
        }

        TerminationChecker<RelativeInteger> powerSetSumChecker = new PowerSetSumChecker<>(RelativeInteger.opSet, RelativeInteger.opSet);

        dataFlowResults.forEach((key, result) -> {
            AssignMapping<RelativeInteger> critical = result.get(key.first());
            AssignMapping<RelativeInteger> untangled = result.get(key.second());

            // remove nonexistent edges
            Set<Integer> keySet = new HashSet<>(critical.keySet());
            HeapConfiguration heap = stateSpace.getState(key.first()).getHeap();

            for (Integer nt : keySet) {
                if (!heap.nonterminalEdges().contains(nt)) {
                    critical.unassign(nt);
                }
            }

            for (Integer nt : keySet) {
                if (!heap.nonterminalEdges().contains(nt)) {
                    untangled.unassign(nt);
                }
            }

            terminationResults.put(key.first(), powerSetSumChecker.check(critical, untangled));
        });
    }

    @Override
    public void logSummary() {
        if (scene().options().isPredicateMode()) {
            if (terminationResults.isEmpty()) {
                logHighlight("Predicate analysis:");
                logSum("No critical states found");
                return;
            }

            StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
            boolean hasFinalState = stateSpace.getFinalStates().size() > 0;
            logSum("+-------------------------+------------------+");
            logHighlight("| Critical state          | Termination      |");
            logSum("+-------------------------+------------------+");

            terminationResults.forEach((critical, result) -> {
                StringBuilder summary;
                if (result.isEmpty()) {
                    summary = new StringBuilder(hasFinalState ? "Maybe" : "No");
                } else {
                    HeapConfiguration heap = stateSpace.getState(critical).getHeap();
                    summary = new StringBuilder("Yes ");
                    summary.append(result
                            .stream()
                            .map(nt -> heap.attachedNodesOf(nt).toString())
                            .collect(Collectors.joining(" + ")));
                }

                if (critical.toString().length() <= 23 && summary.length() <= 16) {
                    logSum("| " + critical +
                            new String(new char[23 - critical.toString().length()]).replace("\0", " ") +
                            " | " +
                            new String(new char[16 - summary.length()]).replace("\0", " ") + summary +
                            " |");
                } else {
                    logSum("| " + critical + " | " + summary + " |");
                }
            });

            logSum("+-------------------------+------------------+");
        }
    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
