package de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.ScopeExtractor;
import de.rwth.i2.attestor.procedures.ScopedHeap;
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
