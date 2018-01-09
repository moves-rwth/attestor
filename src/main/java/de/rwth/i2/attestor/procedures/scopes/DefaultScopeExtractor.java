package de.rwth.i2.attestor.procedures.scopes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.methodExecution.ScopeExtractor;
import de.rwth.i2.attestor.procedures.methodExecution.ScopedHeap;
import de.rwth.i2.attestor.util.Pair;

public class DefaultScopeExtractor extends SceneObject implements ScopeExtractor {

    private String scopeName;

    public DefaultScopeExtractor(SceneObject sceneObject, String scopeName) {
        super(sceneObject);
        this.scopeName = scopeName;
    }

    @Override
    public ScopedHeap extractScope(HeapConfiguration heapConfiguration) {

        ReachableFragmentComputer computer = new ReachableFragmentComputer(this, scopeName, heapConfiguration);
        Pair<HeapConfiguration, Pair<HeapConfiguration, Integer>> fragments = computer.prepareInput();
        return new InternalScopedHeap(fragments.first(), fragments.second().first(), fragments.second().second());
    }
}
