package de.rwth.i2.attestor.ipa;


import java.util.*;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

public class IpaAbstractMethod extends AbstractMethod {

    final IpaContractCollection contracts = new IpaContractCollection();
    private boolean isRecursive = false;

    public IpaAbstractMethod(SceneObject sceneObject, String displayName) {

        super(sceneObject);
        super.setDisplayName(displayName);
    }

    @Override
    public Set<ProgramState> getFinalStates(ProgramState input, SymbolicExecutionObserver observer) {

        try {
            return getResultStates(input, observer);
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("No contract found");
        }
    }

    public void addContracts(HeapConfiguration precondition, List<HeapConfiguration> postconditions) {

        contracts.addPostconditionsTo(precondition, postconditions);
    }

    @Override
    public Set<ProgramState> getResult(ProgramState input, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        observer.update(this, input);
        return getResultStates(input, observer);
    }

    public Set<ProgramState> getResultStates(ProgramState input, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        Set<ProgramState> result = new LinkedHashSet<>();

        for (HeapConfiguration postConfig : getIPAResult(input, observer)) {
            ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
            state.setProgramCounter(0);
            result.add(state);
        }

        return result;
    }

    public List<HeapConfiguration> getIPAResult(ProgramState input, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration currentConfig = input.getHeap();
        Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> splittedConfig = prepareInput(currentConfig);
        HeapConfiguration reachableFragment = splittedConfig.first();
        HeapConfiguration remainingFragment = splittedConfig.second().first();
        int placeholderPos = splittedConfig.second().second();

        Set<HeapConfiguration> postconditions;
        if (!contracts.hasMatchingPrecondition(reachableFragment) || !isReuseResultsEnabled()) {

            postconditions = computeContract(input, reachableFragment, observer);

        } else {
            int[] reordering = contracts.getReordering(reachableFragment);
            remainingFragment = adaptExternalOrdering(reachableFragment, remainingFragment,
                    placeholderPos, reordering);
            postconditions = contracts.getPostconditions(reachableFragment);
        }
        return applyContract(remainingFragment, placeholderPos, postconditions);

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

    public IpaContractCollection getContracts() {

        return contracts;
    }

    public void markAsRecursive() {

        isRecursive = true;
    }

    public boolean isRecursive() {

        return isRecursive;
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
