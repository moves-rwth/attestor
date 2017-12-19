package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.util.Pair;

/**
 * Captures a heap configuration that is partitioned into a reachable fragment with respect to a set of
 * program variables and the remaining fragment. The remaining fragment additionally contains a nonterminal
 * edge such that replacing this edge by the reachable fragment restores the original heap configuration.
 * The ID of this additional nonterminal edge is also stored in a FragmentedHeapConfiguration.
 *
 * @author Christoph
 */
public class FragmentedHeapConfiguration {

    private HeapConfiguration reachablePart;
    private HeapConfiguration remainingPart;
    private int edgeForReachablePart;

    public FragmentedHeapConfiguration(SceneObject sceneObject, HeapConfiguration heapConfiguration,
                                       String nonterminalName) {

        ReachableFragmentComputer computer = new ReachableFragmentComputer(sceneObject,
                nonterminalName, heapConfiguration);

        Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> result = computer.prepareInput();
        reachablePart = result.first();
        remainingPart = result.second().first();
        edgeForReachablePart = result.second().second();
    }

    public HeapConfiguration getReachablePart() {
        return reachablePart;
    }

    public HeapConfiguration getRemainingPart() {
        return remainingPart;
    }

    public int getEdgeForReachablePart() {
        return edgeForReachablePart;
    }

    public HeapConfiguration restore(HeapConfiguration newReachablePart) {

        return remainingPart
                .clone()
                .builder()
                .replaceNonterminalEdge(edgeForReachablePart, newReachablePart)
                .build();
    }
}