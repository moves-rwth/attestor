package de.rwth.i2.attestor.grammar.util;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.LinkedList;
import java.util.List;

public class ExternalNodesPartitioner {

    private HeapConfiguration heapConfiguration;

    int countOldExternals;
    int countNewExternals;

    List<TIntArrayList> partitions = new LinkedList<>();
    boolean[] reductionTentacles;

    public ExternalNodesPartitioner(HeapConfiguration heapConfiguration, boolean[] reductionTentacles) {

        this.heapConfiguration = heapConfiguration;
        this.reductionTentacles = reductionTentacles;

        this.countOldExternals = heapConfiguration.countExternalNodes();
        this.countNewExternals = countOldExternals-1;

        generate(new TIntArrayList(), 0);
    }

    public List<TIntArrayList> getPartitions() {

        return partitions;
    }

    private void generate(TIntArrayList chosen, int start) {

        if(chosen.size() >= countOldExternals) {

            for(int i=0; i <= chosen.max(); i++) {
                if(!chosen.contains(i)) {
                    return;
                }
            }

            for(TIntArrayList list : partitions) {
                if(isIsomorphic(list, chosen)) {
                    return;
                }
            }

            partitions.add(chosen);
        } else {
            for(int i=0; i < countNewExternals; i++) {
                TIntArrayList updated = new TIntArrayList(chosen);

                if(updated.contains(i)) {

                    // check node types
                    if(!hasMatchingNodeTypes(updated, i)
                            || !hasAtMostOneNonReductionTentacle(updated, i)) {
                        continue;
                    }

                }

                updated.add(i);
                generate(updated, 0);
            }
        }
    }

    private boolean hasMatchingNodeTypes(TIntArrayList current, int newExtPos) {

        int prevExtIndex = current.indexOf(newExtPos);
        Type prevType = getType(prevExtIndex);
        Type nextType = getType(current.size());
        return prevType.equals(nextType);
    }

    private boolean hasAtMostOneNonReductionTentacle(TIntArrayList current, int newExtPos) {

        int pos = current.size();
        if(reductionTentacles[pos]) {
            return true;
        }

        for(int j=0; j < current.size(); j++) {
            if(!reductionTentacles[j] && current.get(j) == newExtPos) {
                return false;
            }
        }
        return true;
    }

    private Type getType(int extIndex) {

        int node = heapConfiguration.externalNodeAt(extIndex);
        return heapConfiguration.nodeTypeOf(node);
    }

    private boolean isIsomorphic(TIntArrayList left, TIntArrayList right) {

        if(left.size() != right.size()) {
            return false;
        }

        TIntIntMap map = new TIntIntHashMap();
        for(int i=0; i < left.size(); i++) {
            int l = left.get(i);
            int r = right.get(i);

            if(map.containsKey(l)) {
                if(map.get(l) != r) {
                    return false;
                }
            } else {
                if(map.containsValue(r)) {
                    return false;
                }
                map.put(l, r);
            }
        }
        return true;
    }

}
