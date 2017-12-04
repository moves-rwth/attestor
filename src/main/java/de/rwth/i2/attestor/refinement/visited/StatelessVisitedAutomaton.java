package de.rwth.i2.attestor.refinement.visited;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.markings.Marking;
import de.rwth.i2.attestor.markings.Markings;
import de.rwth.i2.attestor.refinement.StatelessHeapAutomaton;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.semantics.util.VariableScopes;
import gnu.trove.iterator.TIntIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Set;

public class StatelessVisitedAutomaton implements StatelessHeapAutomaton {

    private static final Logger logger = LogManager.getLogger("StatelessVisitedAutomaton");

    private final Marking marking;

    public StatelessVisitedAutomaton(Marking marking) {

        this.marking = marking;
    }

    @Override
    public Set<String> transition(HeapConfiguration heapConfiguration) {

        int markedNode = heapConfiguration.variableTargetOf(marking.getUniversalVariableName());

        if (markedNode == HeapConfiguration.INVALID_ELEMENT) {
            logger.error("Found a heap configuration that does not contain a marking.");
            return Collections.emptySet();
        }

        TIntIterator iter = heapConfiguration.attachedVariablesOf(markedNode).iterator();
        while (iter.hasNext()) {
            int var = iter.next();
            String name = VariableScopes.getName(heapConfiguration.nameOf(var));
            if (!Constants.isConstant(name) && !Markings.isMarking(name)) {
                return Collections.singleton("{ visited }");
            }
        }

        return Collections.emptySet();
    }

}
