package de.rwth.i2.attestor.phases.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.WorklistAlgorithm;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.RelativeInteger;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PredicateAnalysisPhase extends AbstractPhase {
    private final Map<Integer, Boolean> terminationResults = new HashMap<>();
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
                    new PredicateAnalysis<>(critical, adapter, RelativeInteger.opSet, abstractionRule, RelativeInteger.get(50));

            dataFlowResults.put(new Pair<>(critical, analysis.getUntangled()), solver.solve(analysis));
        }

        TerminationChecker<RelativeInteger> singleEdgeChecker = new SingleEdgeChecker<>(RelativeInteger.opSet);
        TerminationChecker<RelativeInteger> indexSumChecker = new IndexSumChecker<>(RelativeInteger.opSet, RelativeInteger.opSet);

        dataFlowResults.forEach((key, result) -> {
            AssignMapping<RelativeInteger> critical = result.get(key.first());
            AssignMapping<RelativeInteger> untangled = result.get(key.second());

            if (singleEdgeChecker.check(critical, untangled)) {
                terminationResults.put(key.first(), true);

            } else if (indexSumChecker.check(critical, untangled)) {
                terminationResults.put(key.first(), true);
            } else {
                terminationResults.put(key.first(), false);
            }
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
            String positive = "Yes";
            String negative = stateSpace.getFinalStates().size() > 0 ? "Maybe" : "No";

            logSum("+-------------------------+------------------+");
            logHighlight("| Critical state          | Termination      |");
            logSum("+-------------------------+------------------+");
            terminationResults.forEach((critical, result) ->
                    logSum("| " + critical +
                            new String(new char[23 - critical.toString().length()]).replace("\0", " ") +
                            " | " +
                            new String(new char[16 - (result ? positive : negative).length()]).replace("\0", " ") +
                            (result ? positive : negative) +
                            " |"));
            logSum("+-------------------------+------------------+");
        }
    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
