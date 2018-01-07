package de.rwth.i2.attestor.main.scene;

import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.procedures.MethodExecutor;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;

import java.util.ArrayList;
import java.util.Collection;

public class ConcreteMethod implements Method {

    private String signature;
    private String name = null;
    private boolean isRecursive = false;
    private final Collection<Contract> contracts = new ArrayList<>();
    private Program body = null;
    private MethodExecutor executor = null;

    public ConcreteMethod(String signature) {

        this.signature = signature;
    }

    @Override
    public String getSignature() {

        return signature;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public void setBody(Program body) {
        this.body = body;
    }

    @Override
    public Program getBody() {
        return body;
    }

    @Override
    public boolean isRecursive() {

        return isRecursive;
    }

    @Override
    public void setRecursive(boolean isRecursive) {

        this.isRecursive = isRecursive;
    }

    @Override
    public void addContract(Contract contract) {
        contracts.add(contract);
    }

    @Override
    public Collection<Contract> getContracts() {
        return contracts;
    }

    @Override
    public void setMethodExecution(MethodExecutor executor) {
        this.executor = executor;
    }

    @Override
    public MethodExecutor getMethodExecutor() {

        assert executor != null;
        return executor;
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }

    @Override
    public boolean equals(Object otherObject) {
       if(otherObject == this) {
           return true;
       }
       if(otherObject.getClass() != ConcreteMethod.class) {
           return false;
       }
       ConcreteMethod method = (ConcreteMethod) otherObject;
       return signature.equals(method.signature);
    }

    @Override
    public String toString() {
        return signature;
    }
}
