package de.rwth.i2.attestor.ipa;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.stateSpaceGeneration.SymbolicExecutionObserver;
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
    public Set<ProgramState> getFinalStates(ProgramState input, SymbolicExecutionObserver observer) {

        try {
            HeapConfiguration currentConfig = input.getHeap();
            FragmentedHeapConfiguration fragmentedHc = new FragmentedHeapConfiguration(
                    this, currentConfig, this.toString()
            );
            observer.update(fragmentedHc, input); // this is a hack until the IPA is complete.

            // This is a hack to deal with counterexample generation
            HeapConfiguration precondition = fragmentedHc.getReachablePart();
            if(scene().options().getAbstractionDistance() == 1) {
                precondition = scene()
                        .strategies()
                        .getLenientCanonicalizationStrategy()
                        .canonicalize(precondition);
            } else {
                precondition = scene()
                        .strategies()
                        .getAggressiveCanonicalizationStrategy()
                        .canonicalize(precondition);
            }



            Set<ProgramState> result = new LinkedHashSet<>();
            for (HeapConfiguration postConfig : getContractResult(precondition, fragmentedHc)) {
                ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
                state.setProgramCounter(0);
                result.add(state);
            }

            return result;
        } catch (StateSpaceGenerationAbortedException e) {
            throw new IllegalStateException("No contract found");
        }
    }

    public void addContracts(HeapConfiguration precondition, List<HeapConfiguration> postconditions) {

        contracts.addPostconditionsTp(precondition, postconditions);
    }

    @Override
    public Set<ProgramState> getResult(ProgramState input, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        observer.update(this, input);
        return getResultStates(input, observer);
    }

    Set<ProgramState> getResultStates(ProgramState input, SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration currentConfig = input.getHeap();
        FragmentedHeapConfiguration fragmentedHc = new FragmentedHeapConfiguration(
                this, currentConfig, this.toString()
        );
        observer.update(fragmentedHc, input); // this is a hack until the IPA is complete.

        Set<ProgramState> result = new LinkedHashSet<>();
        for (HeapConfiguration postConfig : getIPAResult(input, observer, fragmentedHc)) {
            ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
            state.setProgramCounter(0);
            result.add(state);
        }

        return result;
    }

    List<HeapConfiguration> getContractResult(HeapConfiguration precondition,
                                         FragmentedHeapConfiguration fragmentedHc)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration remainingFragment = fragmentedHc.getRemainingPart().clone();
        int placeholderPos = fragmentedHc.getEdgeForReachablePart();

        List<HeapConfiguration> postconditions;


        if(!contracts.hasMatchingPrecondition(precondition)) {
            throw new IllegalStateException("Could not find matching contract.");
        }

        int[] reordering = contracts.getReordering(precondition);
        remainingFragment = adaptExternalOrdering(precondition, remainingFragment,
                placeholderPos, reordering);
        postconditions = contracts.getPostconditions(precondition);

        return applyContract(remainingFragment, placeholderPos, postconditions);
    }

    List<HeapConfiguration> getIPAResult(ProgramState input, SymbolicExecutionObserver observer,
                                         FragmentedHeapConfiguration fragmentedHc)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration reachableFragment = fragmentedHc.getReachablePart().clone();
        HeapConfiguration remainingFragment = fragmentedHc.getRemainingPart().clone();
        int placeholderPos = fragmentedHc.getEdgeForReachablePart();

        List<HeapConfiguration> postconditions;
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

    private List<HeapConfiguration> computeContract(ProgramState input, HeapConfiguration reachableFragment,
                                                    SymbolicExecutionObserver observer)
            throws StateSpaceGenerationAbortedException {

        List<HeapConfiguration> postconditions = new ArrayList<>();
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
                                                  List<HeapConfiguration> contracts) {

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
