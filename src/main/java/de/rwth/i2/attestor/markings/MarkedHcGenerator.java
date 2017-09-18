package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import gnu.trove.iterator.TIntIterator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class MarkedHcGenerator {


    private Grammar grammar;
    private Marking marking;

    private Set<HeapConfiguration> markedHeapConfigurations = new HashSet<>();
    private Stack<HeapConfiguration> unexploredUnfoldings = new Stack<>();

    public MarkedHcGenerator(HeapConfiguration initialHc, Grammar grammar, Marking marking) {

        this.grammar = grammar;
        this.marking = marking;

        unexploredUnfoldings.add(initialHc);
        while( !unexploredUnfoldings.isEmpty() ) {

            HeapConfiguration hc = unexploredUnfoldings.pop();

            // TODO First unfold every nonterminal once
            // TODO Then try to add all possible markings on the unfolded hc
            // TODO If at least one new marked graph was generated, add unfolded graph to stack again   

            addPossibleMarkings(hc);
        }




    }

    public Set<HeapConfiguration> getMarkedHcs() {
        return markedHeapConfigurations;
    }

    private void addPossibleMarkings(HeapConfiguration hc) {

        String markingName = marking.getUniversalVariableName();

        TIntIterator nodeIter = hc.nodes().iterator();
        while(nodeIter.hasNext()) {
            int node = nodeIter.next();
            List<SelectorLabel> requiredSelectors = marking.getRequiredSelectors();
            List<SelectorLabel> availableSelectors = hc.selectorLabelsOf(node);

            if(availableSelectors.containsAll(requiredSelectors)) {

                HeapConfigurationBuilder builder = hc.clone().builder();
                builder.addVariableEdge(markingName, node);

                for(SelectorLabel sel : requiredSelectors) {
                    builder.addVariableEdge(
                            marking.getSelectorVariableName(sel.getLabel()),
                            hc.selectorTargetOf(node, sel)
                    );
                }

                // TODO perform full abstraction here first. If

                markedHeapConfigurations.add(builder.build());

            }
        }
    }



}
