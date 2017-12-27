package de.rwth.i2.attestor.main.phases.stateSpaceGeneration;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.PreconditionChecker;
import de.rwth.i2.attestor.ipa.methodExecution.Contract;
import de.rwth.i2.attestor.ipa.methodExecution.ContractMatch;
import de.rwth.i2.attestor.ipa.methodExecution.PreconditionMatchingStrategy;
import gnu.trove.list.array.TIntArrayList;

class InternalPreconditionMatchingStrategy implements PreconditionMatchingStrategy {

    @Override
    public ContractMatch match(Contract contract, HeapConfiguration heapInScope) {

        PreconditionChecker checker = new PreconditionChecker(contract.getPrecondition(), heapInScope);
        if(checker.hasMatching()) {
            int[] externalReordering = getExternalReordering(checker, contract.getPrecondition(), heapInScope);
            return new InternalContractMatch(externalReordering, contract.getPostconditions());
        }
        return ContractMatch.NO_CONTRACT_MATCH;
    }

    private int[] getExternalReordering(PreconditionChecker checker, HeapConfiguration existingPrecondition,
                                        HeapConfiguration heapInScope) {

        Matching matching = checker.getMatching();
        TIntArrayList externalNodes = existingPrecondition.externalNodes();
        int[] result = new int[externalNodes.size()];
        for (int i = 0; i < externalNodes.size(); i++) {

            int matchingNode = matching.match(externalNodes.get(i));
            result[i] = heapInScope.externalIndexOf(matchingNode);
        }

        return result;
    }
}
