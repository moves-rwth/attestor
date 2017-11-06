package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.exampleFactories.ExampleFactoryEmpty;
import de.rwth.i2.attestor.exampleFactories.ExampleFactorySLL;
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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class CounterexampleGeneratorTest {

    @Test
    public void testTrivial() {

        HeapConfiguration input = ExampleHcImplFactory.getList();
        ProgramState initialState = new DefaultProgramState(input.clone());
        Type type = Settings.getInstance().factory().getType("List");
        Program program = getSetNextProgram(type);

        StateSpace traceStateSpace = new InternalStateSpace(2);
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

        ExampleFactoryEmpty factoryEmpty = new ExampleFactoryEmpty();

        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTraceStateSpace(traceStateSpace)
                .setCanonicalizationStrategy(factoryEmpty.getCanonicalization())
                .setMaterializationStrategy(factoryEmpty.getMaterialization())
                .setStateRefinementStrategy(factoryEmpty.getStateRefinement())
                .build();

        HeapConfiguration counterexampleInput = generator.generate();
        assertEquals(input, counterexampleInput);
    }

    private Program getSetNextProgram(Type type) {
        return Program.builder()
                .addStatement(
                        new AssignStmt(
                                new Local(type, "x"),
                                new Field(type, new Local(type, "x"), "next"),
                                -1, Collections.emptySet()
                        )
                )
                .build();
    }

    @Test
    public void testWithMaterialization() {

        ExampleFactoryEmpty factoryEmpty = new ExampleFactoryEmpty();
        ExampleFactorySLL factorySLL = new ExampleFactorySLL();

        Program program = getSetNextProgram(factorySLL.getNodeType());
        Semantics stmt = program.getStatement(0);
        ProgramState initialState = factorySLL.getInitialState();
        initialState.getHeap()
                .builder()
                .addVariableEdge("0-x", initialState.getHeap().nodes().get(0))
                .build();

        List<ProgramState> mat = factorySLL
                .getMaterialization()
                .materialize(initialState.clone(), stmt.getPotentialViolationPoints());
        ProgramState materialized = null;
        for(ProgramState s : mat) {
            if(!s.getHeap().nonterminalEdges().isEmpty()) {
                materialized = s;
                break;
            }
        }
        assertNotNull(materialized);

        ProgramState finalState = null;
        try {
            finalState = stmt.computeSuccessors(
                    materialized.clone(), factoryEmpty.getSemanticsOptionsSupplier().get(null)
            ).iterator().next();
            finalState = factorySLL.getCanonicalization().canonicalize(stmt, finalState);
        } catch (NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e) {
            fail();
        }

        StateSpace traceStateSpace = new InternalStateSpace(100);
        traceStateSpace.addInitialState(initialState);
        traceStateSpace.addState(materialized);
        traceStateSpace.addMaterializationTransition(initialState, materialized);
        traceStateSpace.addState(finalState);
        traceStateSpace.setFinal(finalState);
        traceStateSpace.addControlFlowTransition(materialized, finalState);

        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTraceStateSpace(traceStateSpace)
                .setCanonicalizationStrategy(factorySLL.getCanonicalization())
                .setMaterializationStrategy(factorySLL.getMaterialization())
                .setStateRefinementStrategy(factoryEmpty.getStateRefinement())
                .build();

        HeapConfiguration counterexampleInput = generator.generate();
        HeapConfiguration expected = factorySLL.getListofLengthAtLeastOne();
        expected.builder()
                .addVariableEdge("0-x", expected.nodes().get(0))
                .build();
        assertEquals(expected, counterexampleInput);
    }
}
