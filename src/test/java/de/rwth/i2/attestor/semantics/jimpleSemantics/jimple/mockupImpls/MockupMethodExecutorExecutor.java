package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.util.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.contracts.InternalContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.NonRecursiveMethodExecutor;
import de.rwth.i2.attestor.procedures.scopes.DefaultScopeExtractor;

public class MockupMethodExecutorExecutor extends NonRecursiveMethodExecutor {

    public MockupMethodExecutorExecutor(SceneObject sceneObject, Method method) {
        super(
                new DefaultScopeExtractor(sceneObject, method.getName()),
                new InternalContractCollection(new InternalPreconditionMatchingStrategy()),
                new MockupContractGenerator(method.getBody())
        );
    }
}
