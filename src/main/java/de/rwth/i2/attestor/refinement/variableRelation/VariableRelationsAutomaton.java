package de.rwth.i2.attestor.refinement.variableRelation;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.Set;

public class VariableRelationsAutomaton implements StatelessHeapAutomaton {

    private final String lhs;
    private final String rhs;
    private final String lhsField;
    private final String rhsField;

    private final Set<String> equalityAPs;
    private final Set<String> inequalityAPs;

    public VariableRelationsAutomaton(String lhs, String rhs) {

        if (lhs.contains(".")) {
            String[] split = lhs.split("[.]");
            this.lhs = returnHotfix(split[0].trim());
            this.lhsField = split[1].trim();
        } else {
            this.lhs = returnHotfix(lhs);
            this.lhsField = null;
        }

        if (rhs.contains(".")) {
            String[] split = rhs.split("[.]");
            this.rhs = returnHotfix(split[0].trim());
            this.rhsField = split[1].trim();
        } else {
            this.rhs = returnHotfix(rhs);
            this.rhsField = null;
        }

        equalityAPs = Collections.singleton("{ " + lhs + " == " + rhs + " }");
        inequalityAPs = Collections.singleton("{ " + lhs + " != " + rhs + " }");
    }

    private String returnHotfix(String name) {

        if (name.startsWith("return")) {
            return "@" + name;
        }
        return name;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int lhsNode = HeapConfiguration.INVALID_ELEMENT;
        int rhsNode = HeapConfiguration.INVALID_ELEMENT;

        // This is a hotfix because the LTL parser does not like @ symbols...

        TIntIterator varIter = heapConfiguration.variableEdges().iterator();
        while (varIter.hasNext()
                && (lhsNode == HeapConfiguration.INVALID_ELEMENT || rhsNode == HeapConfiguration.INVALID_ELEMENT)) {
            int var = varIter.next();

            // remove scoping information first
            String name = heapConfiguration.nameOf(var);

            if (lhsNode == HeapConfiguration.INVALID_ELEMENT) {
                lhsNode = getNode(heapConfiguration, var, name, lhs, lhsField);
            }

            if (rhsNode == HeapConfiguration.INVALID_ELEMENT) {
                rhsNode = getNode(heapConfiguration, var, name, rhs, rhsField);
            }
        }

        if (lhsNode == HeapConfiguration.INVALID_ELEMENT || rhsNode == HeapConfiguration.INVALID_ELEMENT) {
            return Collections.emptySet();
        }

        if (lhsNode == rhsNode) {
            return equalityAPs;
        }

        return inequalityAPs;
    }

    private int getNode(HeapConfiguration hc, int varEdge, String hcVar, String var, String field) {

        if (hcVar.equals(var)) {
            int node = hc.targetOf(varEdge);
            if (field != null && node != HeapConfiguration.INVALID_ELEMENT) {

                for (SelectorLabel sel : hc.selectorLabelsOf(node)) {
                    if (sel.getLabel().equals(lhsField)) {
                        return hc.selectorTargetOf(
                                node,
                                sel
                        );
                    }
                }
                return HeapConfiguration.INVALID_ELEMENT;
            }
            return node;
        }
        return HeapConfiguration.INVALID_ELEMENT;
    }
}
