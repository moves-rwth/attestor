package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.ipa.contracts.InternalContractCollection;
import de.rwth.i2.attestor.ipa.methodExecution.ContractBasedMethod;
import de.rwth.i2.attestor.ipa.methods.Method;
import de.rwth.i2.attestor.ipa.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.main.phases.stateSpaceGeneration.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.main.scene.SceneObject;

public class MockupMethodExecutor extends ContractBasedMethod {

    public MockupMethodExecutor(SceneObject sceneObject, Method method) {
        super(
                new DefaultScopeExtractor(sceneObject, method.getName()),
                new InternalContractCollection(new InternalPreconditionMatchingStrategy()),
                new MockupContractGenerator(method.getBody())
        );
    }
}
