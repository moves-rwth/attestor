package de.rwth.i2.attestor.counterexampleGeneration;

import de.rwth.i2.attestor.exampleFactories.ExampleFactoryEmpty;
import de.rwth.i2.attestor.exampleFactories.ExampleFactorySLL;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.TerminalStatement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.SimpleAbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.StaticInvokeHelper;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Field;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Local;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.strategies.NoStateLabelingStrategy;
import de.rwth.i2.attestor.strategies.StateSpaceBoundedAbortStrategy;
import de.rwth.i2.attestor.strategies.defaultGrammarStrategies.DefaultProgramState;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.util.NotSufficientlyMaterializedException;
import de.rwth.i2.attestor.util.SingleElementUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class CounterexampleGeneratorTest {

    @Test
    public void testTrivial() {

        HeapConfiguration input = ExampleHcImplFactory.getList();
        ProgramState initialState = new DefaultProgramState(input.clone());
        Type type = Settings.getInstance().factory().getType("List");
        Program program = getSetNextProgram(type);

        ProgramState finalState = null;
        try {
            finalState = program.getStatement(0)
                    .computeSuccessors(initialState.clone(), new MockupSemanticsOptions()).iterator().next();
        } catch (NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e) {
            fail();
        }

        MockupTrace trace = new MockupTrace();
        trace.addState(initialState)
                .addState(finalState);

        ExampleFactoryEmpty factoryEmpty = new ExampleFactoryEmpty();
        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTrace(trace)
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
        ProgramState initialState = getInitialState();

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

        MockupTrace trace = new MockupTrace();
        trace.addState(initialState)
                .addState(finalState);

        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTrace(trace)
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

    private ProgramState getInitialState() {

        ExampleFactorySLL factorySLL = new ExampleFactorySLL();
        ProgramState initialState = factorySLL.getInitialState();
        initialState.getHeap()
                .builder()
                .addVariableEdge("0-x", initialState.getHeap().nodes().get(0))
                .build();

        return initialState;
    }

    @Test
    public void testWithProcedures() {

        ExampleFactoryEmpty factoryEmpty = new ExampleFactoryEmpty();
        ExampleFactorySLL factorySLL = new ExampleFactorySLL();

        AssignInvoke invokeStmt = getProcedure();
        Program program = Program.builder()
                .addStatement(invokeStmt)
                .addStatement(new Skip(2))
                .addStatement(new TerminalStatement())
                .build();

        ProgramState initialState = getInitialState();

        ProgramState finalState = null;
        try {
            Set<ProgramState> successors = invokeStmt.computeSuccessors(initialState.clone(),
                    new SemanticsOptions() {
                        @Override
                        public void update(Object handler, ProgramState input) {

                        }

                        @Override
                        public StateSpace generateStateSpace(Program program, ProgramState input) throws StateSpaceGenerationAbortedException {
                            ProgramState initialState = new DefaultProgramState(input.getHeap(), input.getScopeDepth());
                            initialState.setProgramCounter(0);
                            return StateSpaceGenerator.builder()
                                    .addInitialState(initialState)
                                    .setProgram(program)
                                    .setStateRefinementStrategy(s -> s)
                                    .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                                    .setStateLabelingStrategy(new NoStateLabelingStrategy())
                                    .setMaterializationStrategy(factorySLL.getMaterialization())
                                    .setCanonizationStrategy(factorySLL.getCanonicalization())
                                    .setStateCounter( s -> {} )
                                    .setExplorationStrategy((s,sp) -> true)
                                    .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                                    .setSemanticsOptionsSupplier(s -> this)
                                    .build()
                                    .generate();
                        }

                        @Override
                        public boolean isDeadVariableEliminationEnabled() {
                            return false;
                        }
                    }
            );
            assertEquals(2, successors.size());
            for(ProgramState s : successors) {
                if(s.getHeap().countNonterminalEdges() == 2) {
                    finalState = s;
                    break;
                }
            }
        } catch (NotSufficientlyMaterializedException | StateSpaceGenerationAbortedException e) {
            fail();
        }

        MockupTrace trace = new MockupTrace();
        trace.addState(initialState)
                .addState(finalState)
                .addState(finalState.shallowCopyUpdatePC(2))
                .addState(finalState.shallowCopyUpdatePC(-1));

        CounterexampleGenerator generator = CounterexampleGenerator
                .builder()
                .setProgram(program)
                .setTrace(trace)
                .setCanonicalizationStrategy(factorySLL.getCanonicalization())
                .setMaterializationStrategy(factorySLL.getMaterialization())
                .setStateRefinementStrategy(factoryEmpty.getStateRefinement())
                .build();

        HeapConfiguration counterexampleInput = generator.generate();

        HeapConfiguration expected = factorySLL
                .getListofLengthAtLeastOne()
                .builder()
                .addVariableEdge("0-x", 0)
                .build();

        assertEquals(expected, counterexampleInput);
    }

    private AssignInvoke getProcedure() {

        AbstractMethod procedure = new SimpleAbstractMethod("method");

        ExampleFactorySLL factorySLL = new ExampleFactorySLL();

        Local varY = new Local(factorySLL.getNodeType(), "y");
        Field fieldN = new Field(factorySLL.getNodeType(), varY, factorySLL.getNextSel().getLabel());

        List<Semantics> controlFlow = new ArrayList<>();
        controlFlow.add( new IdentityStmt(1, varY, "@parameter0:"));

        controlFlow.add( new AssignStmt(varY, fieldN, 2, Collections.emptySet()));
        controlFlow.add( new ReturnValueStmt(varY, factorySLL.getNodeType()) );
        procedure.setControlFlow( controlFlow );

        Local varX = new Local(factorySLL.getNodeType(), "x");
        StaticInvokeHelper invokeHelper = new StaticInvokeHelper(SingleElementUtil.createList(varX),
                SingleElementUtil.createList("x"));
        return new AssignInvoke(varX, procedure, invokeHelper, 1);
    }
}
