package de.rwth.i2.attestor.refinement.variableRelation;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.strategies.VariableScopes;
import gnu.trove.iterator.TIntIterator;

import java.util.Collections;
import java.util.Set;

public class VariableRelationsAutomaton implements StatelessHeapAutomaton {

    private final String lhs;
    private final String rhs;
    private final String field;

    private final Set<String> equalityAPs;
    private final Set<String> inequalityAPs;

    public VariableRelationsAutomaton(String lhs, String rhs) {

        if(lhs.contains(".")) {
            String[] split = lhs.split("[.]");
            this.lhs = split[0].trim();
            this.field = split[1].trim();
        } else {
            this.lhs = lhs;
            this.field = null;
        }

        this.rhs = rhs;

        equalityAPs = Collections.singleton("{ " + lhs + " == " + rhs + " }");
        inequalityAPs = Collections.singleton("{ " + lhs + " != " + rhs + " }");
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int lhsNode = HeapConfiguration.INVALID_ELEMENT;
        int rhsNode = HeapConfiguration.INVALID_ELEMENT;

        // This is a hotfix because the LTL parser does not like @ symbols...
        String lhsName = lhs;
        if(lhs.startsWith("return")) {
            lhsName = "@" + lhsName;
        }

        TIntIterator varIter = heapConfiguration.variableEdges().iterator();
        while(varIter.hasNext()
                && (lhsNode == HeapConfiguration.INVALID_ELEMENT || rhsNode == HeapConfiguration.INVALID_ELEMENT)) {
            int var = varIter.next();

            // remove scoping information first
            String name = VariableScopes.getName(heapConfiguration.nameOf(var));


            if(name.equals(lhsName)) {
                lhsNode = heapConfiguration.targetOf(var);
                if(field != null && lhsNode != HeapConfiguration.INVALID_ELEMENT) {

                    boolean foundSelector = false;
                    for(SelectorLabel sel : heapConfiguration.selectorLabelsOf(lhsNode)) {
                        if(sel.getLabel().equals(field)) {
                            lhsNode = heapConfiguration.selectorTargetOf(
                                    lhsNode,
                                    sel
                            );
                            foundSelector = true;
                            break;
                        }
                    }
                    if(!foundSelector) {
                        lhsNode = HeapConfiguration.INVALID_ELEMENT;
                    }
                }
            }

            if(name.equals(rhs)) {
                rhsNode = heapConfiguration.targetOf(var);
            }
        }

        if(lhsNode == HeapConfiguration.INVALID_ELEMENT || rhsNode == HeapConfiguration.INVALID_ELEMENT) {
            return Collections.emptySet();
        }

        if(lhsNode == rhsNode) {
            return equalityAPs;
        }

        return inequalityAPs;
    }
}
