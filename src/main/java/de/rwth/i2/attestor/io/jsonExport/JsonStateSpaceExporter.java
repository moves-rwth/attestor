package de.rwth.i2.attestor.io.jsonExport;

import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import org.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

/**
 * Exports a state space to a JSON file.
 *
 * @author Christoph
 */
public class JsonStateSpaceExporter implements StateSpaceExporter {

    private Writer writer;
    private JSONWriter jsonWriter;
    private StateSpace stateSpace;
    private Program program;

    private Set<ProgramState> states;
    private Set<ProgramState> initialStates;
    private Set<ProgramState> finalStates;

    private Map<ProgramState, Integer> incomingEdges = new HashMap<>();
    private Set<ProgramState> isEssentialState = new HashSet<>();

    public JsonStateSpaceExporter(Writer writer) {

        this.writer = writer;
    }

    @Override
    public void export(StateSpace stateSpace, Program program) throws IOException {

        jsonWriter = new JSONWriter(writer);
        this.stateSpace = stateSpace;
        this.program = program;

        states = stateSpace.getStates();
        initialStates = stateSpace.getInitialStates();
        finalStates = stateSpace.getFinalStates();
        computeIdsAndIncomingEdges();

        jsonWriter.object()
                .key("elements")
                .object()
                .key("nodes")
                .array();
        addNodes();
        jsonWriter.endArray().key("edges").array();
        addStateSpaceEdges();
        addTransitiveEdges();
        jsonWriter.endArray().endObject().endObject();
        writer.close();
    }

    private void computeIdsAndIncomingEdges() {

        for(ProgramState s : states) {
            for(ProgramState succ : stateSpace.getControlFlowSuccessorsOf(s)) {
                if(incomingEdges.containsKey(succ)) {
                    incomingEdges.put(succ, incomingEdges.get(succ) + 1);
                } else {
                    incomingEdges.put(succ, 1);
                }
            }
            for(ProgramState succ : stateSpace.getMaterializationSuccessorsOf(s)) {
                if(incomingEdges.containsKey(succ)) {
                    incomingEdges.put(succ, incomingEdges.get(succ) + 1);
                } else {
                    incomingEdges.put(succ, 1);
                }
            }
        }
    }

    private void addNodes() {

        for(ProgramState s : states) {
            int i = s.getStateSpaceId();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(i);
            jsonWriter.key("type");
            if(initialStates.contains(s)) {
                jsonWriter.value("initialState");
            } else if(finalStates.contains(s)) {
                jsonWriter.value("finalState");
            } else if(incomingEdges.containsKey(s) && incomingEdges.get(s) > 1) {
                jsonWriter.value("mergeState");
            } else {
                jsonWriter.value("state");
            }
            jsonWriter.key("propositions").array();
            for(String ap : s.getAPs()) {
                jsonWriter.value(ap);
            }
            jsonWriter.endArray();
            jsonWriter.key("statement").value(program.getStatement(s.getProgramCounter()).toString());
            jsonWriter.key("essential");
            boolean essential = !incomingEdges.containsKey(s)
                    || incomingEdges.get(s) != 1
                    || stateSpace.getMaterializationSuccessorsOf(s).size() != 1
                    || stateSpace.getControlFlowSuccessorsOf(s).size() != 1;
            if(essential) {
                isEssentialState.add(s);
            }
            jsonWriter.value(essential);
            jsonWriter.key("size").value(s.getHeap().countNodes());
            jsonWriter.endObject().endObject();
        }
    }

    private void addStateSpaceEdges() {

        for(ProgramState predState : states)  {
            int source = predState.getStateSpaceId();
            for (ProgramState succ : stateSpace.getControlFlowSuccessorsOf(predState)) {

                int target = succ.getStateSpaceId();
                String label = "" ;
                String type = "execution";

                jsonWriter.object().key("data").object()
                        .key("source").value(source)
                        .key("target").value(target)
                        .key("type").value(type)
                        .key("label").value(label)
                        .endObject().endObject();
            }
            for (ProgramState succ : stateSpace.getMaterializationSuccessorsOf(predState)) {

                int target = succ.getStateSpaceId();
                String label = "" ;
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

    private void addTransitiveEdges() {

        for(ProgramState s : stateSpace.getStates()) {
            if(isEssentialState.contains(s)) {
                int source = s.getStateSpaceId();
                for(ProgramState succ : computeEssentialSuccessors(s)) {
                    int target = succ.getStateSpaceId();
                    jsonWriter.object().key("data").object()
                            .key("source").value(source)
                            .key("target").value(target)
                            .key("type").value("transitive")
                            .key("label").value("")
                            .endObject().endObject();
                }
            }
        }
    }

    private List<ProgramState> computeEssentialSuccessors(ProgramState state) {

        List<ProgramState> reachableEssentials = new ArrayList<>();
        for(ProgramState succ : stateSpace.getControlFlowSuccessorsOf(state)) {
            if(isEssentialState.contains(succ)) {
                reachableEssentials.add(succ);
            } else {
                reachableEssentials.addAll( computeEssentialSuccessors(succ)  );
            }
        }
        for(ProgramState succ : stateSpace.getMaterializationSuccessorsOf(state)) {
            if(isEssentialState.contains(succ)) {
                reachableEssentials.add(succ);
            } else {
                reachableEssentials.addAll( computeEssentialSuccessors(succ)  );
            }
        }

        return reachableEssentials;
    }
}
