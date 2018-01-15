package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContractCollection;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.NonRecursiveMethodExecutor;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.procedures.Method;

public class MockupMethodExecutorExecutor extends NonRecursiveMethodExecutor {

    public MockupMethodExecutorExecutor(SceneObject sceneObject, Method method) {
        super(
                new DefaultScopeExtractor(sceneObject, method.getName()),
                new InternalContractCollection(new InternalPreconditionMatchingStrategy()),
                new MockupContractGenerator(method.getBody())
        );
    }
}
