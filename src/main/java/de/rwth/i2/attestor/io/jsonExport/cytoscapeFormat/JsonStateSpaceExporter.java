package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.json.JSONStringer;
import org.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

/**
 * Exports a state space to a JSON file.
 *
 * @author Christoph
 */
public class JsonStateSpaceExporter implements StateSpaceExporter {

    private Writer writer;
    private JSONWriter jsonWriter;
    private JSONStringer jsonStringer;
    private StateSpace stateSpace;
    private Program program;

    private Set<ProgramState> states;
    private Set<ProgramState> initialStates;
    private Set<ProgramState> finalStates;

    private TIntIntMap incomingEdgesOfStates;
    private TIntSet isEssentialStateId;

    public JsonStateSpaceExporter(Writer writer) {

        this.writer = writer;
    }

    public JsonStateSpaceExporter() {
        this.writer = null;
    }

    @Override
    public void export(StateSpace stateSpace, Program program) throws IOException {

        jsonWriter = new JSONWriter(writer);
        this.stateSpace = stateSpace;
        this.program = program;

        states = stateSpace.getStates();
        initialStates = stateSpace.getInitialStates();
        finalStates = stateSpace.getFinalStates();

        incomingEdgesOfStates = new TIntIntHashMap(states.size());
        isEssentialStateId = new TIntHashSet(states.size());

        computeNumberOfIncomingEdges();

        jsonWriter.object()
                .key("elements")
                .object()
                .key("nodes")
                .array();
        addNodes(jsonWriter);
        jsonWriter.endArray().key("edges").array();
        addStateSpaceEdges(jsonWriter);
        jsonWriter.endArray().endObject().endObject();
        writer.close();
    }

    @Override
    public String exportForReport(StateSpace stateSpace, Program program) throws IOException {

        jsonStringer = new JSONStringer();
        this.stateSpace = stateSpace;
        this.program = program;

        states = stateSpace.getStates();
        initialStates = stateSpace.getInitialStates();
        finalStates = stateSpace.getFinalStates();

        incomingEdgesOfStates = new TIntIntHashMap(states.size());
        isEssentialStateId = new TIntHashSet(states.size());

        computeNumberOfIncomingEdges();

        jsonStringer.object()
                .key("nodes")
                .array();
        addNodes(jsonStringer);
        jsonStringer.endArray().key("edges").array();
        addStateSpaceEdges(jsonStringer);
        jsonStringer.endArray().endObject();

        return jsonStringer.toString();

    }

    private void computeNumberOfIncomingEdges() {

        for (ProgramState s : states) {
            int id = s.getStateSpaceId();
            TIntIterator iterator = stateSpace.getControlFlowSuccessorsIdsOf(id).iterator();
            while (iterator.hasNext()) {
                int successorId = iterator.next();
                if (incomingEdgesOfStates.containsKey(successorId)) {
                    incomingEdgesOfStates.put(successorId, incomingEdgesOfStates.get(successorId) + 1);
                } else {
                    incomingEdgesOfStates.put(successorId, 1);
                }
            }
            iterator = stateSpace.getMaterializationSuccessorsIdsOf(id).iterator();
            while (iterator.hasNext()) {
                int successorId = iterator.next();
                if (incomingEdgesOfStates.containsKey(successorId)) {
                    incomingEdgesOfStates.put(successorId, incomingEdgesOfStates.get(successorId) + 1);
                } else {
                    incomingEdgesOfStates.put(successorId, 1);
                }
            }
        }
    }

    private void addNodes(JSONWriter jsonWriter) {

        for (ProgramState s : states) {
            int id = s.getStateSpaceId();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(id);
            jsonWriter.key("type");
            if (initialStates.contains(s)) {
                jsonWriter.value("initialState");
            } else if (finalStates.contains(s)) {
                jsonWriter.value("finalState");
            } else if (incomingEdgesOfStates.containsKey(id) && incomingEdgesOfStates.get(id) > 1) {
                jsonWriter.value("mergeState");
            } else {
                jsonWriter.value("state");
            }
            jsonWriter.key("propositions").array();
            for (String ap : s.getAPs()) {
                jsonWriter.value(ap);
            }
            jsonWriter.endArray();
            final int pc = s.getProgramCounter();
            final String statement = program.getStatement(pc).toString();
            jsonWriter.key("statement").value(statement);
            jsonWriter.endObject().endObject();
        }
    }

    private void addStateSpaceEdges(JSONWriter jsonWriter) {

        for (ProgramState predecessorState : states) {
            int source = predecessorState.getStateSpaceId();
            TIntIterator successorIterator = stateSpace.getControlFlowSuccessorsIdsOf(source).iterator();
            while (successorIterator.hasNext()) {
                int target = successorIterator.next();
                String label = "";
                String type = "execution";

                jsonWriter.object().key("data").object()
                        .key("source").value(source)
                        .key("target").value(target)
                        .key("type").value(type)
                        .key("label").value(label)
                        .endObject().endObject();
            }
            successorIterator = stateSpace.getMaterializationSuccessorsIdsOf(source).iterator();
            while (successorIterator.hasNext()) {
                int target = successorIterator.next();
                String label = "";
                String type = "materialization";

                jsonWriter.object().key("data").object()
                        .key("source").value(source)
                        .key("target").value(target)
                        .key("type").value(type)
                        .key("label").value(label)
                        .endObject().endObject();
            }
        }
    }

}
