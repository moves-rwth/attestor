package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.Scene;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A listener to construct a HeapConfiguration from a parse tree representing a
 * symbolic heap formula possibly with pure formulas and predicate calls.
 *
 * @Christoph
 */
public class HeapConfigurationExtractor extends SeparationLogicBaseListener {

    /**
     * The Scene providing access to factories and global settings.
     */
    private final Scene scene;

    private final VariableUnification variableUnification;

    /**
     * The last HeapConfiguration, which was successfully constructed.
     */
    private HeapConfiguration heapConfiguration;

    /**
     * The builder used to construct a HeapConfiguration.
     */
    private HeapConfigurationBuilder builder;

    /**
     * Mapping from variable names to their corresponding node identifier in
     * the HeapConfiguration.
     */
    private Map<String, Integer> variableToNodeId;

    /**
     * The name of the last encountered variable.
     */
    private String lastVariable;

    private SelectorLabel lastSelectorLabel;

    private String lastSelectorSource;

    private Nonterminal lastNonterminal;

    private List<String> parameters;

    public HeapConfigurationExtractor(@Nonnull Scene scene, @Nonnull VariableUnification variableUnification) {

        this.scene = scene;
        this.variableUnification = variableUnification;
    }

    public HeapConfiguration getHeapConfiguration() {

        return heapConfiguration;
    }

    /**
     * Prepares construction of a new HeapConfiguration corresponding to the
     * given parse tree.
     */
    @Override public void enterHeap(SeparationLogicParser.HeapContext ctx) {

        builder = scene.createHeapConfiguration().builder();
        variableToNodeId = new HashMap<>();

        for(String variable : variableUnification.getUniqueVariableNames()) {

            Type type = scene.getType(variableUnification.getType(variable));
            int nodeId = builder.addSingleNode(type);
            variableToNodeId.put(variable, nodeId);

            if(variableUnification.getProgramVariableNames().contains(variable)) {
                builder.addVariableEdge(variable, nodeId);
            }
        }
    }

    /**
     * Finishes construction of a new HeapConfiguration corresponding to the
     * given parse tree.
     */
    @Override
    public void exitHeap(SeparationLogicParser.HeapContext ctx) {

        heapConfiguration = builder.build();
        builder = null;
        variableToNodeId = null;
    }

    /**
     * Stores the name of the encountered variable.
     */
    @Override
    public void enterVariable(SeparationLogicParser.VariableContext ctx) {

        lastVariable = ctx.getText();
    }

    @Override public void enterSelector(SeparationLogicParser.SelectorContext ctx) {

        lastSelectorSource = lastVariable;
        lastVariable = null;
        lastSelectorLabel = scene.getSelectorLabel(ctx.getText());
    }

    /**
     * Creates a new pointer and the involved nodes (if necessary).
     */
    @Override
    public void exitPointer(SeparationLogicParser.PointerContext ctx) {

        if(lastVariable == null) {
            lastVariable = "null";
        }

        int source = getNode(lastSelectorSource);
        int target = getNode(lastVariable);

        builder.addSelector(source, lastSelectorLabel, target);

        lastVariable = null;
        lastSelectorLabel = null;
        lastSelectorSource = null;
    }

    private int getNode(String variableName) {

        return variableToNodeId.get(variableUnification.getUniqueName(variableName));
    }

    @Override
    public void enterPredicateCall(SeparationLogicParser.PredicateCallContext ctx) {

        parameters = new ArrayList<>();
    }

    @Override
    public void exitPredicateCall(SeparationLogicParser.PredicateCallContext ctx) {

        TIntArrayList attachedNodes = new TIntArrayList();
        for(String param : parameters) {
            attachedNodes.add(getNode(param));
        }

        builder.addNonterminalEdge(lastNonterminal, attachedNodes);

        lastNonterminal = null;
        parameters = null;
    }

    @Override
    public void exitParameter(SeparationLogicParser.ParameterContext ctx) {

        if(lastVariable == null) {
            parameters.add("null");
        } else {
            parameters.add(lastVariable);
        }
        lastVariable = null;
    }

    @Override
    public void enterPredicateSymbol(SeparationLogicParser.PredicateSymbolContext ctx) {

        lastNonterminal = scene.getNonterminal(ctx.getText());
    }
}
