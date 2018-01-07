package de.rwth.i2.attestor.ipa;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.procedures.scopes.ReachableFragmentComputer;
import de.rwth.i2.attestor.programState.defaultState.DefaultProgramState;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.impl.*;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

import java.util.*;

public class IpaAbstractMethod extends AbstractMethod {

    final IpaContractCollection contracts = new IpaContractCollection();
    private boolean isRecursive = false;

    public IpaAbstractMethod(SceneObject sceneObject, String displayName) {

        super(sceneObject);
        super.setDisplayName(displayName);
    }

    @Override
    public Set<ProgramState> getFinalStates(ProgramState input, ProgramState callingState, SymbolicExecutionObserver observer) {

        try {
            return getResultStates(input, callingState, observer);
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("No contract found");
        }
    }

    public void addContracts(HeapConfiguration precondition, List<HeapConfiguration> postconditions) {

        contracts.addPostconditionsTo(precondition, postconditions);
    }

    @Override
    public Set<ProgramState> getResult(ProgramState input, ProgramState callingState, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        observer.update(this, input);
        return getResultStates(input, callingState, observer);
    }

    public Set<ProgramState> getResultStates(ProgramState input, ProgramState callingState, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        Set<ProgramState> result = new LinkedHashSet<>();

        for (HeapConfiguration postConfig : getIPAResult(input, callingState, observer)) {
            ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
            state.setProgramCounter(0);
            result.add(state);
        }

        return result;
    }
    
    /**
     * Splits the input heap into the reachable fragment, which is the part of the heap visible for the method,
     * and the remaining fragment, where the reachable fragment is replaced by a special nonterminal.
     * The reachable fragment serves as precondition to the method call. 
     * This method then looks up or computes the corresponding postconditions and applies them to the remaining fragment.
     * 
     * In case this method is recursive, an empty result may be returned and the calling state is registered for continuation
     * once the postconditions for this contract changes.
     * 
     * With the setting isReuseResultsEnabled() contracts are always recomputed
     * 
     * @param input the program state serving as input to the method, i.e. with parameters and base value in place
     * @param callingState the program state on which the call statement is executed. 
     * @param observer the current semantics observer used to generate the sub-stateSpaces
     * @return the list of resulting states
     * @throws StateSpaceGenerationAbortedException
     */
    public List<HeapConfiguration> getIPAResult(ProgramState input, ProgramState callingState, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration currentConfig = input.getHeap();
        Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> splittedConfig = prepareInput(currentConfig);
        HeapConfiguration reachableFragment = splittedConfig.first();
        HeapConfiguration remainingFragment = splittedConfig.second().first();
        int placeholderPos = splittedConfig.second().second();

        Set<HeapConfiguration> postconditions;
        if ( !contracts.hasMatchingPrecondition(reachableFragment) || !isReuseResultsEnabled()) {
        	if( this.isRecursive() ){
        		postconditions = handleRecursion(input, callingState, reachableFragment);
        	}else{
        		postconditions = computeContract(input, reachableFragment, observer);
        	}

        } else {
            int[] reordering = contracts.getReordering(reachableFragment);
            remainingFragment = adaptExternalOrdering(reachableFragment, remainingFragment,
                    placeholderPos, reordering);
            postconditions = contracts.getPostconditions(reachableFragment);
            if( this.isRecursive() ){
            	scene().recursionManager().registerAsDependentOf(callingState, this, input.shallowCopyWithUpdateHeap(reachableFragment));
            }
        }
        return applyContract(remainingFragment, placeholderPos, postconditions);

    }

	private Set<HeapConfiguration> handleRecursion(ProgramState input, ProgramState callingState,
			HeapConfiguration reachableFragment) {
		
		InterproceduralAnalysisManager recursionManager = scene().recursionManager();
		
		final ProgramState precoditionState = scene().createProgramState(reachableFragment);
		
		recursionManager.registerToCompute(this, precoditionState);
		recursionManager.registerAsDependentOf(callingState, this, precoditionState);
		
		Set<HeapConfiguration> postconditions = new LinkedHashSet<>();
		contracts.addContract(reachableFragment, postconditions);
		return postconditions;
	}

    private Set<HeapConfiguration> computeContract(ProgramState input, HeapConfiguration reachableFragment, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        Set<HeapConfiguration> postconditions = new HashSet<>();
        ProgramState initialState = input.shallowCopyWithUpdateHeap(reachableFragment);
        StateSpace stateSpace = observer.generateStateSpace(method, initialState);

        for (ProgramState finalState : stateSpace.getFinalStates()) {
            postconditions.add(finalState.getHeap());
        }

        if (isReuseResultsEnabled()) {
            contracts.addContract(reachableFragment, postconditions);
        }

        return postconditions;
    }

    /**
     * @param input
     * @return <reachableFragment,remainingFragment>
     */
    protected Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> prepareInput(HeapConfiguration input) {

        ReachableFragmentComputer helper = new ReachableFragmentComputer(this, this.toString(), input);
        return helper.prepareInput();
    }

    private List<HeapConfiguration> applyContract(HeapConfiguration remainingFragment,
                                                  int contractPlaceholderEdge,
                                                  Set<HeapConfiguration> contracts) {

        List<HeapConfiguration> result = new ArrayList<>();
        for (HeapConfiguration contract : contracts) {
            HeapConfigurationBuilder builder = remainingFragment.clone().builder();
            builder.replaceNonterminalEdge(contractPlaceholderEdge, contract);
            result.add(builder.build());
        }

        return result;
    }

    protected HeapConfiguration adaptExternalOrdering(HeapConfiguration reachableFragment,
                                                      HeapConfiguration remainingFragment,
                                                      int placeholderPosition,
                                                      int[] reordering
    )
            throws IllegalArgumentException {

        TIntArrayList oldTentacles = remainingFragment.attachedNodesOf(placeholderPosition);
        TIntArrayList newTentacles = new TIntArrayList();
        for (int aReordering : reordering) {
            newTentacles.add(oldTentacles.get(aReordering));
        }

        Nonterminal label = remainingFragment.labelOf(placeholderPosition);
        return remainingFragment.builder().removeNonterminalEdge(placeholderPosition)
                .addNonterminalEdge(label, newTentacles).build();

    }

    public IpaContractCollection getIPAContracts() {

        return contracts;
    }

    public void markAsRecursive() {

        isRecursive = true;
    }

    @Override
    public String getSignature() {

        return null;
    }

    @Override
    public String getName() {

        return null;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public void setBody(Program body) {

    }

    @Override
    public Program getBody() {

        return null;
    }

    public boolean isRecursive() {

        return isRecursive;
    }

    @Override
    public void setRecursive(boolean isRecursive) {

        markAsRecursive();
    }

    @Override
    public void addContract(Contract contract) {

    }

    @Override
    public Collection<Contract> getContracts() {

        return null;
    }

    @Override
    public void setMethodExecution(MethodExecutor methodExecution) {

    }

    @Override
    public MethodExecutor getMethodExecutor() {

        return new MethodExecutor() {
            @Override
            public void addContract(Contract contract) {

            }

            @Override
            public Collection<ProgramState> getResultStates(ProgramState callingState, ProgramState input) {

                try {

                    return IpaAbstractMethod.this.getResultStates(input, callingState,
                            new SymbolicExecutionObserver() {
                                @Override
                                public void update(Object handler, ProgramState input) {

                                }

                                @Override
                                public StateSpace generateStateSpace(Program program, ProgramState input) throws StateSpaceGenerationAbortedException {

                                    ProgramState initialState = new DefaultProgramState(input.getHeap());
                                    initialState.setProgramCounter(0);
                                    return StateSpaceGenerator.builder()
                                            .addInitialState(initialState)
                                            .setProgram(program)
                                            .setStateRefinementStrategy(new NoStateRefinementStrategy())
                                            .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                                            .setStateLabelingStrategy(new NoStateLabelingStrategy())
                                            .setMaterializationStrategy(
                                                    (state, potentialViolationPoints) -> new ArrayList<>()
                                            )
                                            .setCanonizationStrategy(state -> state.clone())
                                            .setStateCounter(s -> {
                                            })
                                            .setExplorationStrategy((s, sp) -> true)
                                            .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                                            .setPostProcessingStrategy(new NoPostProcessingStrategy())
                                            .build()
                                            .generate();
                                }

                                @Override
                                public boolean isDeadVariableEliminationEnabled() {

                                    return false;
                                }

                                @Override
                                public StateSpace continueStateSpace(StateSpace stateSpace, Program program, ProgramState continuationPoint) throws StateSpaceGenerationAbortedException {

                                    return StateSpaceGenerator.builder()
                                            .addInitialState(continuationPoint)
                                            .setInitialStateSpace(stateSpace)
                                            .setProgram(program)
                                            .setStateRefinementStrategy(new NoStateRefinementStrategy())
                                            .setAbortStrategy(new StateSpaceBoundedAbortStrategy(500, 50))
                                            .setStateLabelingStrategy(new NoStateLabelingStrategy())
                                            .setMaterializationStrategy(
                                                    (state, potentialViolationPoints) -> new ArrayList<>()
                                            )
                                            .setCanonizationStrategy(state -> state.clone())
                                            .setStateCounter(s -> {
                                            })
                                            .setExplorationStrategy((s, sp) -> true)
                                            .setStateSpaceSupplier(() -> new InternalStateSpace(100))
                                            .setPostProcessingStrategy(new NoPostProcessingStrategy())
                                            .build()
                                            .generate();
                                }
                            }
                    );
                } catch (StateSpaceGenerationAbortedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    public static final class Factory extends SceneObject {

        private Map<String, IpaAbstractMethod> knownMethods = new LinkedHashMap<>();

        public Factory(Scene scene) {

            super(scene);
        }

        public IpaAbstractMethod get(String signature) {

            if (!knownMethods.containsKey(signature)) {
                knownMethods.put(signature, new IpaAbstractMethod(this, signature));
            }
            return knownMethods.get(signature);
        }
    }


}
