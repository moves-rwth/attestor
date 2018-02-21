package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.mockupImpls;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalContractCollection;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.InternalPreconditionMatchingStrategy;
import de.rwth.i2.attestor.phases.symbolicExecution.procedureImpl.scopes.DefaultScopeExtractor;
import de.rwth.i2.attestor.phases.symbolicExecution.recursive.interproceduralAnalysis.NonRecursiveMethodExecutor;
import de.rwth.i2.attestor.procedures.Method;

public class MockupMethodExecutor extends NonRecursiveMethodExecutor {

    public MockupMethodExecutor(SceneObject sceneObject, Method method) {
        super(	method,
                new DefaultScopeExtractor(sceneObject, method.getName()),
                new InternalContractCollection(new InternalPreconditionMatchingStrategy()),
                new ProcedureRegistryStub( sceneObject )
        );
    }
}
