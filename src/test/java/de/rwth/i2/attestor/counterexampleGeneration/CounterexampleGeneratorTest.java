package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.AssignStmt;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.MockupSemanticsOptions;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CounterexampleGeneratorTest {

    private HeapConfiguration input;
    private ProgramState initialState;
    private Type type;
    private Program program;
    private StateSpace traceStateSpace;

    @Test
    public void testTrivial() {

        input = ExampleHcImplFactory.getList();
        initialState = new DefaultProgramState(input.clone());
        type = Settings.getInstance().factory().getType("List");

        setupProgramTrivial();
        setupTraceStateSpaceTrivial();

        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTraceStateSpace(traceStateSpace)
                .setCanonicalizationStrategy((sem, s) -> s)
                .setMaterializationStrategy((s, vio) -> Collections.emptyList())
                .setStateRefinementStrategy(s -> s)
                .build();

        HeapConfiguration counterexampleInput = generator.generate();
        assertEquals(input, counterexampleInput);
    }

    private void setupProgramTrivial() {
        List<Semantics> statements = new ArrayList<>();
        Local x = new Local(type, "x");
        Semantics stmt = new AssignStmt(x, new Field(type, x, "next"), -1, Collections.emptySet());
        statements.add(stmt);
        program = new Program(statements);
    }

    private void setupTraceStateSpaceTrivial() {
        traceStateSpace = new InternalStateSpace(2);
        traceStateSpace.addInitialState(initialState);

        ProgramState finalState = null;
        try {
            finalState = program.getStatement(0)
                    .computeSuccessors(initialState.clone(), new MockupSemanticsOptions()).iterator().next();
        } catch (NotSufficientlyMaterializedException e) {
            fail();
        } catch (StateSpaceGenerationAbortedException e) {
            fail();
        }
        traceStateSpace.addState(finalState);
        traceStateSpace.addControlFlowTransition(initialState, finalState);
        traceStateSpace.setFinal(finalState);
    }
}
