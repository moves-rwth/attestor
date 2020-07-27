package de.rwth.i2.attestor.phases.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.WorklistAlgorithm;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.RelativeInteger;
import de.rwth.i2.attestor.predicateAnalysis.SLListAbstractionRule;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.predicateAnalysis.PredicateAnalysis;
import de.rwth.i2.attestor.predicateAnalysis.StateSpaceAdapter;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PredicateAnalysisPhase extends AbstractPhase {

    private final boolean enabled;
    private Map<Integer, Map<Integer, AssignMapping<RelativeInteger>>> results = new HashMap<>();

    public PredicateAnalysisPhase(Scene scene) {
        super(scene);
        enabled = scene.options().isPredicateMode();
    }

    @Override
    public String getName() {
        return "Predicate Analysis";
    }

    @Override
    public void executePhase() {
        if (!enabled) {
            return;
        }

        Program program = getPhase(ProgramTransformer.class).getProgram();
        StateSpace stateSpace = getPhase(StateSpaceTransformer.class).getStateSpace();
        StateSpaceAdapter adapter = new StateSpaceAdapter(stateSpace, program, Collections.singleton(GotoStmt.class));

        Grammar grammar = getPhase(GrammarTransformer.class).getGrammar();
        SLListAbstractionRule abstractionRule = new SLListAbstractionRule(grammar);  // TODO(mkh) create programmatically

        EquationSolver<AssignMapping<RelativeInteger>> solver = new WorklistAlgorithm<>();

        for (int critical : adapter.getCriticalLabels()) {
            PredicateAnalysis<RelativeInteger> analysis =
                    new PredicateAnalysis<>(critical, adapter, RelativeInteger.opSet, abstractionRule);

            results.put(critical, solver.solve(analysis));
        }
    }

    @Override
    public void logSummary() {
        if (enabled) {
            // TODO(mkh)
        }
    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
