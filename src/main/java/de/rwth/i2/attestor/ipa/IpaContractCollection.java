package de.rwth.i2.attestor.ipa;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.Matching;
import de.rwth.i2.attestor.graph.heap.matching.PreconditionChecker;
import de.rwth.i2.attestor.util.Pair;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is essentially a hashMap from IpaPrecondtion to List&#60;HeapConfiguration&#62;
 * but it returns the complete Entry instead of only the value.
 * This is necessary to manage the reordering of tentacles.
 *
 * @author Hannah
 */
public class IpaContractCollection {

    PreconditionChecker lastUsedChecker; //to avoid double computations
    private Map<Integer, List<IpaContract>> map = new HashMap<>();

    public List<HeapConfiguration> getPostconditions(HeapConfiguration reachableFragment) {

        int hashCode = reachableFragment.hashCode();
        if (!map.containsKey(hashCode)) {
            map.put(hashCode, new ArrayList<>());
        }
        List<IpaContract> contracts = map.get(hashCode);
        for (IpaContract contract : contracts) {
            if (match(contract.precondition, reachableFragment)) {
                return contract.postconditions;
            }
        }

        return null;
    }

    public int[] getReordering(HeapConfiguration reachableFragment) {

        for (IpaContract contract : map.get(reachableFragment.hashCode())) {

            if (match(contract.precondition, reachableFragment)) {
                return getReordering(contract.precondition, reachableFragment);
            }
        }

        return null;
    }

    /**
     * Computes the reordering of external nodes necessary to match the two configs.
     * For an example, consider the following two graphs:
     * (1) -> (2) -> (3)  <br>
     * (2) -> (1) -> (3) <br>
     * The resulting reordering would be [2,1,3]
     *
     * @param matchingConfig The configuration that matches this precondition up do external reordering
     * @return an array specifying the necessary reordering
     * @throws IllegalArgumentException if the matchingConfig does *not* already match the
     *                                  precondition up do external reordering.
     */
    int[] getReordering(HeapConfiguration existingPrecondition,
                        HeapConfiguration matchingConfig)
            throws IllegalArgumentException {

        PreconditionChecker checker = lastUsedChecker;
        if (checker == null || checker.getTarget() != matchingConfig || checker.getPattern() != existingPrecondition) { //checker is not valid for this input
            checker = new PreconditionChecker(existingPrecondition, matchingConfig);
            if (!checker.hasMatching()) {
                throw new IllegalArgumentException(); //input has to be matching
            }
        }

        Matching matching = checker.getMatching();
        TIntArrayList externalNodes = existingPrecondition.externalNodes();
        int[] result = new int[externalNodes.size()];
        for (int i = 0; i < externalNodes.size(); i++) {

            int matchingNode = matching.match(externalNodes.get(i));
            result[i] = matchingConfig.externalIndexOf(matchingNode);
        }

        return result;
    }

    public void addContract(HeapConfiguration precondition, List<HeapConfiguration> postconditions) {

        int hashCode = precondition.hashCode();
        if (!map.containsKey(hashCode)) {
            map.put(hashCode, new ArrayList<>());
        }

        map.get(hashCode).add(new IpaContract(precondition, postconditions));
    }

    public void addPostconditionsTp(HeapConfiguration precondition, List<HeapConfiguration> postconditions) {

        if (!this.hasMatchingPrecondition(precondition)) {

            this.addContract(precondition, new ArrayList<>());
        }
        List<HeapConfiguration> currentPostconditions = this.getPostconditions(precondition);
        currentPostconditions.addAll(postconditions);
    }

    public boolean hasMatchingPrecondition(HeapConfiguration reachableFragment) {

        int hashCode = reachableFragment.hashCode();
        if (!map.containsKey(hashCode)) {
            return false;
        }

        for (IpaContract contract : map.get(hashCode)) {
            if (match(contract.precondition, reachableFragment)) {
                return true;
            }
        }
        return false;
    }

    boolean match(HeapConfiguration existingPrecondition, HeapConfiguration reachableFragment) {

        PreconditionChecker checker = new PreconditionChecker(existingPrecondition, reachableFragment);
        if (checker.hasMatching()) {
            lastUsedChecker = checker;
            return true;
        }
        return false;
    }

    public List<Pair<HeapConfiguration, List<HeapConfiguration>>> getContractList() {

        List<Pair<HeapConfiguration, List<HeapConfiguration>>> res = new ArrayList<>();

        for (List<IpaContract> contractsWithSameHash : map.values()) {
            for (IpaContract contract : contractsWithSameHash) {
                res.add(new Pair<>(contract.precondition, contract.postconditions));
            }
        }
        return res;
    }

    private class IpaContract {

        public HeapConfiguration precondition;
        public List<HeapConfiguration> postconditions;
        public IpaContract(HeapConfiguration precondition, List<HeapConfiguration> postconditions2) {

            this.precondition = precondition;
            this.postconditions = postconditions2;
        }
    }
}
