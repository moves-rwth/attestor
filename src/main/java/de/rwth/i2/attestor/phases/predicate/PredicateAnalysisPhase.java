package de.rwth.i2.attestor.phases.predicate;

import de.rwth.i2.attestor.dataFlowAnalysis.EquationSolver;
import de.rwth.i2.attestor.dataFlowAnalysis.WorklistAlgorithm;
import de.rwth.i2.attestor.domain.AssignMapping;
import de.rwth.i2.attestor.domain.AugmentedInteger;
import de.rwth.i2.attestor.domain.RelativeIndex;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.main.AbstractPhase;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.predicateAnalysis.StateSpaceAdapter;
import de.rwth.i2.attestor.predicateAnalysis.relativeIntegerPA.RelativeIntegerPA;
import de.rwth.i2.attestor.predicateAnalysis.relativeIntegerPA.SLListAbstractionRule;
import de.rwth.i2.attestor.phases.transformers.GrammarTransformer;
import de.rwth.i2.attestor.phases.transformers.ProgramTransformer;
import de.rwth.i2.attestor.phases.transformers.StateSpaceTransformer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.GotoStmt;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PredicateAnalysisPhase extends AbstractPhase {

    private final boolean enabled;
    private Map<Integer, Map<Integer, AssignMapping<Integer, RelativeIndex<AugmentedInteger>>>> results = new HashMap<>();

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

        EquationSolver<AssignMapping<Integer, RelativeIndex<AugmentedInteger>>> solver = new WorklistAlgorithm<>();

        for (int critical : adapter.getCriticalLabels()) {
            RelativeIntegerPA analysis =
                    new RelativeIntegerPA(critical, adapter, abstractionRule);

            results.put(critical, solver.solve(analysis));
        }
    }

    @Override
    public void logSummary() {
        if (enabled) {

        } else {

        }
    }

    @Override
    public boolean isVerificationPhase() {
        return true;
    }
}
