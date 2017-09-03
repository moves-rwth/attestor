package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.strategies.NoStateLabelingStrategy;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;

import java.util.ArrayList;

public class StateSpaceFactoryHelper {

    public static AbstractMethod.StateSpaceFactory get() {

        return (program, input, scopeDepth) -> {
            ProgramState initialState = new DefaultProgramState(input, scopeDepth);
            initialState.setProgramCounter(0);
            return StateSpaceGenerator.builder()
                    .addInitialState(initialState)
                    .setProgram(program)
                    .setStateRefinementStrategy(s -> s)
                    .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                    .setStateLabelingStrategy(new NoStateLabelingStrategy())
                    .setMaterializationStrategy(
                            (state, potentialViolationPoints) -> new ArrayList<>()
                    )
                    .setCanonizationStrategy(
                            (semantics, conf) -> {
                                return conf.clone();
                            }
                    )
                    .setStateCounter( s -> {} )
                    .build()
                    .generate();
        };
    }

}
