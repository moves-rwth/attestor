package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.InvokeCleanup;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

import java.util.Collections;
import java.util.Set;

public class CounterexampleSemanticsOptions implements SemanticsOptions {

    private StateSpaceGenerator stateSpaceGenerator;
    private CounterexampleGenerator counterexampleGenerator;

    private ProgramState requiredFinalState = null;

    private int requiredNoOfFinalStates = 1;

    CounterexampleSemanticsOptions(StateSpaceGenerator stateSpaceGenerator, CounterexampleGenerator counterexampleGenerator) {

        this.stateSpaceGenerator = stateSpaceGenerator;
        this.counterexampleGenerator = counterexampleGenerator;
    }

    @Override
    public void update(Object handler, ProgramState input) {

        if(input.getScopeDepth() == 0 && handler instanceof InvokeCleanup) {
            updateInvoke((InvokeCleanup) handler, input);
        } else if(handler instanceof AbstractMethod) {
            updateMethod( (AbstractMethod) handler, input);
        }
    }

    private void updateInvoke(InvokeCleanup stmt, ProgramState input) {
        requiredFinalState = counterexampleGenerator.getTraceSuccessor(input);
        counterexampleGenerator.setLastProcedureInvokeCleanup(stmt);
    }

    private void updateMethod(AbstractMethod method, ProgramState input) {

        method.setReuseResults(false);
        counterexampleGenerator.setLastProcedureInitialState(input);
        if(requiredFinalState != null) {
            requiredNoOfFinalStates = 1;
            counterexampleGenerator.setLastProcedureFinalStates(
                    Collections.singleton(requiredFinalState)
            );
        } else {
            Set<ProgramState> finalStates = method.getFinalStates(input.getHeap());
            counterexampleGenerator.setLastProcedureFinalStates(finalStates);
            requiredNoOfFinalStates = finalStates.size();
        }
    }

    @Override
    public StateSpace generateStateSpace(Program program, ProgramState input)
            throws StateSpaceGenerationAbortedException {

        return StateSpaceGenerator
                .builder()
                .setAbortStrategy(stateSpaceGenerator.getAbortStrategy())
                .setCanonizationStrategy(stateSpaceGenerator.getCanonizationStrategy())
                .setMaterializationStrategy(stateSpaceGenerator.getMaterializationStrategy())
                .setStateLabelingStrategy(stateSpaceGenerator.getStateLabelingStrategy())
                .setStateRefinementStrategy(stateSpaceGenerator.getStateRefinementStrategy())
                .setDeadVariableElimination(stateSpaceGenerator.isDeadVariableEliminationEnabled())
                .setBreadthFirstSearchEnabled(stateSpaceGenerator.isBreadthFirstSearchEnabled())
                .setExplorationStrategy((s,sp) -> sp.getFinalStates().size() < requiredNoOfFinalStates)
                .setStateSpaceSupplier(stateSpaceGenerator.getStateSpaceSupplier())
                .setSemanticsOptionsSupplier(stateSpaceGenerator.getSemanticsOptionsSupplier())
                .setStateCounter(stateSpaceGenerator.getTotalStatesCounter())
                .setProgram(program)
                .addInitialState(input)
                .build()
                .generate();

    }

    @Override
    public boolean isDeadVariableEliminationEnabled() {

        return false;
    }
}
