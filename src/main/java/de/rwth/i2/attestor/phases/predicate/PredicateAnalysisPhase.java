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
import de.rwth.i2.attestor.predicateAnalysis.AbstractionRule;
import de.rwth.i2.attestor.predicateAnalysis.PredicateAnalysis;
import de.rwth.i2.attestor.predicateAnalysis.StateSpaceAdapter;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PredicateAnalysisPhase extends AbstractPhase {

    private Map<Integer, Map<Integer, AssignMapping<RelativeInteger>>> results = new HashMap<>();

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
        AbstractionRule<RelativeInteger> abstractionRule = getPhase(AbstractionRuleTransformer.class).getAbstractionRule();
        StateSpaceAdapter adapter = new StateSpaceAdapter(stateSpace, program, Collections.singleton(GotoStmt.class));
        EquationSolver<AssignMapping<RelativeInteger>> solver = new WorklistAlgorithm<>();

        for (int critical : adapter.getCriticalLabels()) {
            PredicateAnalysis<RelativeInteger> analysis =
                    new PredicateAnalysis<>(
                            critical,
                            adapter,
                            RelativeInteger.opSet,
                            abstractionRule,
                            RelativeInteger.opSet.greatestElement()
                    );

            results.put(critical, solver.solve(analysis));
        }
    }

    @Override
    public void logSummary() {
        if (scene().options().isPredicateMode()) {
            // TODO(mkh): generate certificate
        }
    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
