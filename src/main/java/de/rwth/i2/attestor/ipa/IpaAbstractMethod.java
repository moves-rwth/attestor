package de.rwth.i2.attestor.ipa;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.ipa.scopes.ScopedHeapConfigurationPair;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;
import obsolete.SymbolicExecutionObserver;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
            ScopedHeapConfigurationPair fragmentedHc = new ScopedHeapConfigurationPair(
                    this, currentConfig, this.toString()
            );
            observer.update(fragmentedHc, input); // this is a hack until the IPA is complete.

            // This is a hack to deal with counterexample generation
            // TODO ultimately, we should match preconditions using language inclusion without external ordering.
            HeapConfiguration precondition = fragmentedHc.getHeapInScope();
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
        ScopedHeapConfigurationPair scopedPair = new ScopedHeapConfigurationPair(
                this, currentConfig, this.toString()
        );
        observer.update(scopedPair, input); // this is a hack until the IPA is complete.

        Set<ProgramState> result = new LinkedHashSet<>();
        for (HeapConfiguration postConfig : getIPAResult(input, observer, scopedPair)) {
            ProgramState state = input.shallowCopyWithUpdateHeap(postConfig);
            state.setProgramCounter(0);
            result.add(state);
        }

        return result;
    }

    List<HeapConfiguration> getContractResult(HeapConfiguration precondition,
                                         ScopedHeapConfigurationPair fragmentedHc)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration remainingFragment = fragmentedHc.getHeapOutsideScope().clone();
        int placeholderPos = fragmentedHc.getOutsideScopeEdge();

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
                                         ScopedHeapConfigurationPair fragmentedHc)
            throws StateSpaceGenerationAbortedException {

        HeapConfiguration reachableFragment = fragmentedHc.getHeapInScope().clone();
        HeapConfiguration remainingFragment = fragmentedHc.getHeapOutsideScope().clone();
        int placeholderPos = fragmentedHc.getOutsideScopeEdge();

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
/* TODO
        ReachableFragmentComputer helper = new ReachableFragmentComputer(this, this.toString(), input);
        return helper.prepareInput();
*/
return  null;
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

}
