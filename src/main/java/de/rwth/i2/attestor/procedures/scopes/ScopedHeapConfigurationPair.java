package de.rwth.i2.attestor.procedures.scopes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;
import de.rwth.i2.attestor.util.Pair;

import java.util.Collection;

public class ScopedHeapConfigurationPair {

    private HeapConfiguration heapInScope;
    private HeapConfiguration heapOutsideScope;
    private int outsideScopeEdge;

    public ScopedHeapConfigurationPair(SceneObject sceneObject, HeapConfiguration heapConfiguration,
                                       String nonterminalName) {

        ReachableFragmentComputer computer = new ReachableFragmentComputer(sceneObject,
                nonterminalName, heapConfiguration);

        Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> result = computer.prepareInput();
        heapInScope = result.first();
        heapOutsideScope = result.second().first();
        outsideScopeEdge = result.second().second();
    }

    public HeapConfiguration getHeapInScope() {
        return heapInScope;
    }

    public HeapConfiguration getHeapOutsideScope() {
        return heapOutsideScope;
    }

    public int getOutsideScopeEdge() {
        return outsideScopeEdge;
    }

    public Collection<HeapConfiguration> merge(ContractMatch contractMatch) {

        /*
        return heapOutsideScope
                .clone()
                .builder()
                .replaceNonterminalEdge(outsideScopeEdge, contractMatch.getPostcondition())
                .build();
        */
        return null; // TODO remove class
    }

}