package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class StatelessVisitedAutomaton implements StatelessHeapAutomaton {

    private static final Logger logger = LogManager.getLogger("StatelessVisitedAutomaton");

    private Marking marking;

    public StatelessVisitedAutomaton(Marking marking) {

        this.marking = marking;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = heapConfiguration.variableTargetOf(marking.getUniversalVariableName());

        if(markedNode == HeapConfiguration.INVALID_ELEMENT) {
            logger.error("Found a heap configuration that does not contain a marking.");
            return Collections.emptySet();
        }

        if(heapConfiguration.attachedVariablesOf(markedNode).size() > 1) {
            return Collections.singleton("{ visited }");
        }

        return Collections.emptySet();
    }

}
