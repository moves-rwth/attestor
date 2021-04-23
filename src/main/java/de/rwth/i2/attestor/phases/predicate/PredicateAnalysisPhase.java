package de.rwth.i2.attestor.phases.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.WorklistAlgorithm;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.transformers.AbstractionRuleTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.predicateAnalysis.*;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PredicateAnalysisPhase extends AbstractPhase {
    private final Map<
            Pair<Integer, Integer>,
            Set<Integer>> terminationResults = new HashMap<>();
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

        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        long starttime = System.nanoTime();
        StateSpaceAdapter adapter = new StateSpaceAdapter(stateSpace);

        //noinspection unchecked
        AbstractionRule<RelativeInteger> abstractionRule = getPhase(AbstractionRuleTransformer.class).getAbstractionRule();

        for (int critical : adapter.getCriticalLabels()) {
            PredicateAnalysis<RelativeInteger> analysis = new PredicateAnalysis<>(
                    critical,
                    adapter, RelativeInteger.opSet,
                    abstractionRule,
                    () -> RelativeInteger.getWithAllVariables(20) // RelativeInteger.opSet::greatestElement
            );
            EquationSolver<AssignMapping<RelativeInteger>> solver = new WorklistAlgorithm<>(analysis);
            dataFlowResults.put(new Pair<>(critical, analysis.getUntangled()), solver.solve());
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
            terminationResults.put(key, powerSetSumChecker.check(critical, untangled));
        });
    }

    @Override
    public void logSummary() {
        if (scene().options().isPredicateMode()) {
            if (terminationResults.isEmpty()) {
                logHighlight("Predicate analysis:");
                logSum("No critical states found");
                logSum("+-------------------------+------------------+");
                logSum("Yes States:   []");
                logSum("Maybe States: []");
                logSum("No States:    []");
                logSum("+-------------------------+------------------+");
                return;
            }

            StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
            boolean hasFinalState = stateSpace.getFinalStates().size() > 0;
            Set<Integer> yesStates = new HashSet<>();
            Set<Integer> maybeStates = new HashSet<>();
            Set<Integer> noStates = new HashSet<>();
            terminationResults.forEach((key, result) -> {
                logSum("+-------------------------+------------------+");
                logHighlight("| Critical state          | Termination      |");
                logSum("+-------------------------+------------------+");

                String yesNo;
                if (result.isEmpty()) {
                    if(hasFinalState){
                        yesNo = "Maybe";
                        maybeStates.add(key.first());
                    }else{
                        yesNo = "No";
                        noStates.add(key.first());
                    }
                } else {
                    yesNo = "Yes";
                    yesStates.add(key.first());
                }

                logSum("| " + key.first() +
                        new String(new char[23 - key.first().toString().length()]).replace("\0", " ") +
                        " | " +
                        new String(new char[16 - yesNo.length()]).replace("\0", " ") + yesNo +
                        " |");

                if (!result.isEmpty()) {
                    HeapConfiguration heap = stateSpace.getState(key.first()).getHeap();
                    Map<Integer, String> variables = result
                            .stream()
                            .collect(Collectors.toMap(
                                    Function.identity(),
                                    nt -> dataFlowResults.get(key).get(key.first()).get(nt).getVariables().stream()
                                            .findFirst().get().toString()
                            ));

                    String varSummary = "| " + variables.entrySet()
                            .stream()
                            .map(entry -> heap.attachedNodesOf(entry.getKey()).toString() + " = V" + entry.getValue())
                            .collect(Collectors.joining(", "));

                    logSum("|____________ Edges -> Variables ____________|");
                    if (varSummary.length() < 44) {
                        logSum(varSummary + new String(new char[44 - varSummary.length()]).replace("\0", " ") + " |");
                    } else {
                        logSum(varSummary + " |");
                    }

                    logSum("|_____________ Decreasing Index _____________|");

                    String summary = "| " + result.stream()
                            .map(nt -> "V" + variables.get(nt))
                            .collect(Collectors.joining(" + ")) + " -> " + result.stream().map(nt -> {
                        RelativeInteger index = dataFlowResults.get(key).get(key.second()).get(nt);
                        return index.getVariables().stream()
                                .map(var -> "V" + var).collect(Collectors.joining(" + ")) +
                                (index.getConcrete().getValue() >= 0 ? " + " + index.getConcrete() : " - " + index.getConcrete().getValue() * -1);
                    }).collect(Collectors.joining(" + "));

                    if (summary.length() < 44) {
                        logSum(summary + new String(new char[44 - summary.length()]).replace("\0", " ") + " |");
                    }
                    else {
                        logSum(summary + " |");
                    }
                }
            });
            logSum("+-------------------------+------------------+");
            logSum("Yes States:   "+yesStates);
            logSum("Maybe States: "+maybeStates);
            logSum("No States:    "+noStates);
            logSum("+-------------------------+------------------+");
        }

    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
