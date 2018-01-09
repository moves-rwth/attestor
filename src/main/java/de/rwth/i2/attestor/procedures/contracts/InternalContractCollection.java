package de.rwth.i2.attestor.procedures.contracts;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.methodExecution.Contract;
import de.rwth.i2.attestor.procedures.methodExecution.ContractCollection;
import de.rwth.i2.attestor.procedures.methodExecution.ContractMatch;
import de.rwth.i2.attestor.procedures.methodExecution.PreconditionMatchingStrategy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InternalContractCollection implements ContractCollection {

    private final PreconditionMatchingStrategy preconditionMatchingStrategy;

    private final Map<Integer, Collection<Contract>> contracts;

    public InternalContractCollection(PreconditionMatchingStrategy preconditionMatchingStrategy) {

        this.preconditionMatchingStrategy = preconditionMatchingStrategy;
        this.contracts = new HashMap<>();
    }

    @Override
    public void addContract(Contract contract) {

        int preconditionHash = contract.getPrecondition().hashCode();
        if(!contracts.containsKey(preconditionHash)) {
            Collection<Contract> value = new ArrayList<>();
            value.add(contract);
            contracts.put(preconditionHash, value);
        } else {
            Collection<Contract> hashedContracts = contracts.get(preconditionHash);
            for (Contract c : hashedContracts) {
                ContractMatch match = preconditionMatchingStrategy.match(c, contract.getPrecondition());
                if (match.hasMatch()) {
                    c.addPostconditions(contract.getPostconditions());
                    return;
                }
            }
            hashedContracts.add(contract);
        }
    }

    @Override
    public ContractMatch matchContract(HeapConfiguration precondition) {

        int preconditionHash = precondition.hashCode();
        if(!contracts.containsKey(preconditionHash)) {
            contracts.put(preconditionHash, new ArrayList<>());
        }
        for(Contract contract : contracts.get(preconditionHash)) {
            ContractMatch match = preconditionMatchingStrategy.match(contract, precondition);
            if(match.hasMatch()) {
                return match;
            }
        }

        return ContractMatch.NO_CONTRACT_MATCH;

    }
}
