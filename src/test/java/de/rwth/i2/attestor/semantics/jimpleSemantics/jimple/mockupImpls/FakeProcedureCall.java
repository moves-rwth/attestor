package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.ProcedureCall;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.DepthFirstStateExplorationStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateCounter;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.NoStateRefinementStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.utilStrategies.TerminalStatementFinalStateStrategy;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerator;

import static org.junit.Assert.fail;

public class FakeProcedureCall implements ProcedureCall {

	Method method;
	ProgramState initialState;
	
	public FakeProcedureCall(Method method, ProgramState initialState) {
		this.method = method;
		this.initialState = initialState;
	}

	@Override
	public StateSpace execute() {
		try {
           return StateSpaceGenerator.builder()
                    .addInitialState(initialState)
                    .setProgram(method.getBody())
                    .setCanonizationStrategy(new MockupStateCanonicalizationStrategy())
                    .setMaterializationStrategy(new MockupMaterializationStrategy())
                    .setAbortStrategy(new MockupAbortStrategy())
                    .setPostProcessingStrategy(originalStateSpace -> {
                    })
                    .setStateExplorationStrategy(new DepthFirstStateExplorationStrategy())
                    .setStateRefinementStrategy(new NoStateRefinementStrategy())
                    .setStateLabelingStrategy(new MockupStateLabellingStrategy())
                    .setStateCounter(new NoStateCounter())
                    .setStateSpaceSupplier(new MockupStateSpaceSupplier())
                    .setFinalStateStrategy(new TerminalStatementFinalStateStrategy())
                    .build()
                    .generate();

        } catch (StateSpaceGenerationAbortedException e) {
            fail("Unexpected Exception when executing " + method.getName() );
            return null;
        }
        
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public ProgramState getInput() {
		return initialState;
	}



}
