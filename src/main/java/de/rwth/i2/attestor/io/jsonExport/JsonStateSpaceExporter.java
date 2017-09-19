package de.rwth.i2.attestor.io.jsonExport;

import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import org.json.JSONWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

import org.json.JSONWriter;

import de.rwth.i2.attestor.stateSpaceGeneration.*;

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

    private TIntIntMap incomingEdgesOfStates;
    private TIntSet isEssentialStateId;

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

        incomingEdgesOfStates = new TIntIntHashMap(states.size());
        isEssentialStateId = new TIntHashSet(states.size());

        computeNumberOfIncomingEdges();

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

    private void computeNumberOfIncomingEdges() {

        for(ProgramState s : states) {
            for(ProgramState succ : stateSpace.getControlFlowSuccessorsOf(s)) {
                int succId = succ.getStateSpaceId();
                if(incomingEdgesOfStates.containsKey(succId)) {
                    incomingEdgesOfStates.put(succId, incomingEdgesOfStates.get(succId) + 1);
                } else {
                    incomingEdgesOfStates.put(succId, 1);
                }
            }
            for(ProgramState succ : stateSpace.getMaterializationSuccessorsOf(s)) {
                int succId = succ.getStateSpaceId();
                if(incomingEdgesOfStates.containsKey(succId)) {
                    incomingEdgesOfStates.put(succId, incomingEdgesOfStates.get(succId) + 1);
                } else {
                    incomingEdgesOfStates.put(succId, 1);
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
            } else if(incomingEdgesOfStates.containsKey(i) && incomingEdgesOfStates.get(i) > 1) {
                jsonWriter.value("mergeState");
            } else {
                jsonWriter.value("state");
            }
            jsonWriter.key("propositions").array();
            for(String ap : s.getAPs()) {
                jsonWriter.value(ap);
            }
            jsonWriter.endArray();
            final int pc = s.getProgramCounter();
            final String statement = program.getStatement( pc ).toString();
			jsonWriter.key("statement").value("("+pc+")"+statement);
            jsonWriter.key("essential");
            boolean essential = !incomingEdgesOfStates.containsKey(i)
                    || incomingEdgesOfStates.get(i) != 1
                    || (stateSpace.getMaterializationSuccessorsOf(s).size()
                        + stateSpace.getControlFlowSuccessorsOf(s).size()) != 1;
            if(essential) {
                isEssentialStateId.add(i);
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
            int source = s.getStateSpaceId();
            if(isEssentialStateId.contains(source)) {
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
            int succId = succ.getStateSpaceId();
            if(isEssentialStateId.contains(succId)) {
                reachableEssentials.add(succ);
            } else {
                reachableEssentials.addAll( computeEssentialSuccessors(succ)  );
            }
        }
        for(ProgramState succ : stateSpace.getMaterializationSuccessorsOf(state)) {
            int succId = succ.getStateSpaceId();
            if(isEssentialStateId.contains(succId)) {
                reachableEssentials.add(succ);
            } else {
                reachableEssentials.addAll( computeEssentialSuccessors(succ)  );
            }
        }

        return reachableEssentials;
    }
}
