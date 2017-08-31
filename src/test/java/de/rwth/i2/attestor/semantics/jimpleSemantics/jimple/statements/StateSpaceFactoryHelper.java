package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements;

import java.util.ArrayList;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.SimpleAbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;
import de.rwth.i2.attestor.strategies.GeneralInclusionStrategy;
import de.rwth.i2.attestor.strategies.NoStateLabelingStrategy;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;

public class StateSpaceFactoryHelper {

    public static SimpleAbstractMethod.StateSpaceFactory get() {

        return (program, input, scopeDepth) -> {
            ProgramState initialState = new DefaultProgramState(input, scopeDepth);
            initialState.setProgramCounter(0);
            return StateSpaceGenerator.builder()
                    .addInitialState(initialState)
                    .setProgram(program)
                    .setStateRefinementStrategy(s -> s)
                    .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                    .setInclusionStrategy(new GeneralInclusionStrategy())
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
