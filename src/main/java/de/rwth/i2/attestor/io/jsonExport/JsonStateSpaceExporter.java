package de.rwth.i2.attestor.io.jsonExport;

import de.rwth.i2.attestor.stateSpaceGeneration.*;
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

    private List<ProgramState> states;
    private List<ProgramState> initialStates;
    private List<ProgramState> finalStates;

    private Map<ProgramState, Integer> incomingEdges = new HashMap<>();
    private Map<ProgramState, Integer> stateToId = new HashMap<>();
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
        jsonWriter.endArray().endObject().endObject();;
        writer.close();
    }

    private void computeIdsAndIncomingEdges() {

        for(int i=0; i < states.size(); i++) {
            ProgramState s = states.get(i);
            stateToId.put(s, i);
            for(ProgramState succ : stateSpace.successorsOf(s)) {
                if(incomingEdges.containsKey(succ)) {
                    incomingEdges.put(succ, incomingEdges.get(succ) + 1);
                } else {
                    incomingEdges.put(succ, 1);
                }
            }
        }
    }

    private void addNodes() {

        for(int i=0; i < states.size(); i++) {
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(i);
            ProgramState s = states.get(i);
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
                    || stateSpace.successorsOf(s).size() != 1;
            if(essential) {
                isEssentialState.add(s);
            }
            jsonWriter.value(essential);
            jsonWriter.endObject().endObject();
        }
    }

    private void addStateSpaceEdges() {

        for( Map.Entry<?,?> transition : stateSpace.getSuccessors().entrySet() ) {
            ProgramState predState = (ProgramState) transition.getKey();
            int source = stateToId.get(predState);
            List<?> succList = (List<?>) transition.getValue();
            for (Object succ : succList) {

                StateSuccessor stateSuccessor = (StateSuccessor) succ;
                int target = stateToId.get(stateSuccessor.getTarget());
                String label = stateSuccessor.getLabel();
                String type = (label.isEmpty()) ? "materialization" : "execution";

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
                int source = stateToId.get(s);
                for(ProgramState succ : computeEssentialSuccessors(s)) {
                    int target = stateToId.get(succ);
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
        for(ProgramState succ : stateSpace.successorsOf(state)) {
            if(isEssentialState.contains(succ)) {
                reachableEssentials.add(succ);
            } else {
                reachableEssentials.addAll( computeEssentialSuccessors(succ)  );
            }
        }

        return reachableEssentials;
    }
}
