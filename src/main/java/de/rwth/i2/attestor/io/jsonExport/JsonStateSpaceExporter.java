package de.rwth.i2.attestor.io.jsonExport;

import de.rwth.i2.attestor.stateSpaceGeneration.ProgramState;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceExporter;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSuccessor;
import org.json.JSONWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonStateSpaceExporter implements StateSpaceExporter {

    private Writer writer;

    public JsonStateSpaceExporter(Writer writer) {

        this.writer = writer;
    }

    @Override
    public void export(StateSpace stateSpace) throws IOException {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                .key("elements")
                .object()
                .key("nodes")
                .array();

        writeStates(jsonWriter, stateSpace);


        jsonWriter.endArray().endObject().endObject();;

        writer.close();
    }

    private void writeStates(JSONWriter jsonWriter, StateSpace stateSpace) {

        List<ProgramState> states = stateSpace.getStates();
        List<ProgramState> initialStates = stateSpace.getInitialStates();
        List<ProgramState> finalStates = stateSpace.getFinalStates();

        Map<ProgramState, Integer> incomingEdges = new HashMap<>(states.size());
        Map<ProgramState, Integer> stateToId = new HashMap<>(states.size());

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
            jsonWriter.key("statement").value("TODO");
            jsonWriter.endObject().endObject();
        }

        jsonWriter.endArray().key("edges").array();

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
}
