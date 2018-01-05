package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.main.phases.symbolicExecution.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.contracts.InternalContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractBasedMethod;
import de.rwth.i2.attestor.procedures.scopes.DefaultScopeExtractor;

public class MockupMethodExecutor extends ContractBasedMethod {

    public MockupMethodExecutor(SceneObject sceneObject, Method method) {
        super(
                new DefaultScopeExtractor(sceneObject, method.getName()),
                new InternalContractCollection(new InternalPreconditionMatchingStrategy()),
                new MockupContractGenerator(method.getBody())
        );
    }
}
