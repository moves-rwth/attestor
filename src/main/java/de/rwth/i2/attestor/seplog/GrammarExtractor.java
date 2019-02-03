package de.rwth.i2.attestor.seplog;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
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
 * Creates a hyperedge replacement grammar from a parse tree
 * for systems of inductive predicate definitions.
 *
 * This class requires that all involved nonterminal symbols have already
 * been initialized in the given scene.
 *
 * @author Christoph
 */
public class GrammarExtractor extends SeparationLogicBaseListener {

    /**
     * The scene used to create nonterminals.
     */
    private final Scene scene;

    /**
     * The builder for the current grammar under construction.
     */
    private GrammarBuilder grammarBuilder;

    /**
     * The grammar that has been constructed.
     */
    private Grammar grammar;

    /**
     * The nonterminal on the left-hand side of rules which are currently created.
     */
    private Nonterminal lhs;

    /**
     * Names of variables that correspond to external nodes (in the given order).
     */
    private List<String> externalNames;

    /**
     * Types of variables that correspond to external nodes (in the given order).
     * The length of this list must coincide with the length of list 'externalNames'.
     */
    private List<String> externalTypes;

    /**
     * Builder for the current rule's right-hand side under construction.
     */
    private HeapConfigurationBuilder heapBuilder;

    /**
     * Mapping between variable names used in symbolic heaps and the corresponding node identifier
     * in heap configurations.
     */
    private Map<String, Integer> variableToNodeId;

    /**
     * The name of the last encountered variable while walking through the syntax tree.
     */
    private String lastVariableName;

    /**
     * The type of the last encountered variable while walking through the syntax tree.
     */
    private String lastVariableType;

    /**
     * Stores the last encountered left-hand side of a points-to assertion.
     */
    private String lastPointsToLhs;

    /**
     * Stores the last encountered selector.
     */
    private String lastSelector;

    /**
     * Stores the nonterminal corresponding to the last encountered predicate.
     */
    private Nonterminal lastCallLabel;

    /**
     * Stores the names of predicate call parameters encountered
     * before finishing the last predicate call.
     */
    private List<String> lastCallParameters;

    /**
     * @param scene The scene containing all global settings.
     */
    GrammarExtractor(@Nonnull Scene scene) {
        this.scene = scene;
    }

    /**
     * @return The constructed grammar.
     */
    public Grammar getGrammar() {

        return grammar;
    }

    /**
     * Sets up creation of a new grammar.
     */
    @Override
    public void enterSid(SeparationLogicParser.SidContext ctx) {

        grammarBuilder = Grammar.builder();
    }

    /**
     * Finishes created of a grammar from the given SID parse tree.
     */
    @Override
    public void exitSid(SeparationLogicParser.SidContext ctx) {

        grammar = grammarBuilder.build();
        grammarBuilder = null;
    }

    /**
     * Prepares creation of rules with a new left-hand side.
     */
    @Override
    public void enterSidRule(SeparationLogicParser.SidRuleContext ctx) {

        // we reset the current nonterminal here to make sure that the listener
        // picks up the next nonterminal visited, which is the next nonterminal
        // on the left-hand side of rules.
        lhs = null;
    }

    /**
     * Finishes creation of rules with the current left-hand side.
     */
    @Override
    public void exitSidRule(SeparationLogicParser.SidRuleContext ctx) {

        // cleanup
        lhs = null;
        externalNames = null;
        externalTypes = null;
    }

    /**
     * Prepares collection of the names and types of variables corresponding to external nodes.
     */
    @Override
    public void enterSidRuleHead(SeparationLogicParser.SidRuleHeadContext ctx) {

        externalNames = new ArrayList<>();
        externalTypes = new ArrayList<>();
    }

    /**
     * Prepares creation of a new heap configuration as a right-hand side of a grammar rule.
     */
    @Override
    public void enterSidRuleBody(SeparationLogicParser.SidRuleBodyContext ctx) {

        heapBuilder = scene.createHeapConfiguration().builder();
        variableToNodeId = new HashMap<>();
        createExternalNodes();
    }

    private void createExternalNodes() {

        assert externalTypes.size() == externalNames.size();
        for(int i=0; i < externalNames.size();i++) {

            String typeName = externalTypes.get(i);
            Type type = scene.getType(typeName);
            int nodeId = heapBuilder.addSingleNode(type);
            heapBuilder.setExternal(nodeId);

            String variableName = externalNames.get(i);
            variableToNodeId.put(variableName, nodeId);
        }
    }


    /**
     * Finishes creation of heap configuration serving as the right-hand side of a grammar rule.
     */
    @Override
    public void exitSidRuleBody(SeparationLogicParser.SidRuleBodyContext ctx) {

        HeapConfiguration rhs = heapBuilder.build();

        grammarBuilder.addRule(lhs, rhs);

        heapBuilder = null;
        variableToNodeId = null;
    }

    /**
     * Finishes variable declaration.
     */
    @Override
    public void exitVariableDeclaration(SeparationLogicParser.VariableDeclarationContext ctx) {

        if(isRightHandSideMode()) {

            Type type = scene.getType(lastVariableType);
            int nodeId = heapBuilder.addSingleNode(type);

            if(variableToNodeId.containsKey(lastVariableName)) {
                throw new IllegalStateException("Variable '" + lastVariableName + "' is already declared.");
            }

            variableToNodeId.put(lastVariableName, nodeId);
        } else {

            externalNames.add(lastVariableName);
            externalTypes.add(lastVariableType);
        }

        lastVariableName = null;
        lastVariableType = null;
    }

    /**
     * @return True iff we are currently constructing the right-hand side of a grammar rule.
     */
    private boolean isRightHandSideMode() {

        return heapBuilder != null;
    }

    /**
     * Retrieves the name of a variable.
     */
    @Override
    public void enterVariable(SeparationLogicParser.VariableContext ctx) {

        lastVariableName = ctx.getText();
    }

    /**
     * Retrieves the type of a variable declaration.
     *
     */
    @Override
    public void enterType(SeparationLogicParser.TypeContext ctx) {

        lastVariableType = ctx.getText();
    }

    /**
     * Saves the previously encountered variable as the left-hand side of a
     * points-to assertion and stores the encountered selector.
     */
    @Override
    public void enterSelector(SeparationLogicParser.SelectorContext ctx) {

        lastPointsToLhs = lastVariableName;
        lastVariableName = null;
        lastSelector = ctx.getText();
    }

    /**
     * Adds a new pointer.
     */
    @Override
    public void exitPointer(SeparationLogicParser.PointerContext ctx) {

        if(lastVariableName == null) {
            throw new UnsupportedOperationException("At the moment, " +
                    "'null' has to be modeled as a dedicated free variable.");
        }

        int from = variableToNodeId.get(lastPointsToLhs);
        SelectorLabel selectorLabel = scene.getSelectorLabel(lastSelector);
        scene.labels().addGrammarSelectorLabel(lastSelector);
        int to = variableToNodeId.get(lastVariableName);
        heapBuilder.addSelector(from, selectorLabel, to);

        lastVariableName = null;
        lastPointsToLhs = null;
        lastSelector = null;
    }

    @Override
    public void enterPure(SeparationLogicParser.PureContext ctx) {

        throw new UnsupportedOperationException("Pure formulas in SIDs are not supported.");
    }

    /**
     * Prepares the creation of a new nonterminal edge corresponding to a predicate call.
     */
    @Override
    public void enterPredicateCall(SeparationLogicParser.PredicateCallContext ctx) {

        lastCallParameters = new ArrayList<>();
    }

    /**
     * Finishes the creation of a new nonterminal edge corresponding to a predicate call.
     */
    @Override
    public void exitPredicateCall(SeparationLogicParser.PredicateCallContext ctx) {

        if(lastCallLabel.getRank() != lastCallParameters.size()) {
            throw new IllegalArgumentException("Predicate '" + lastCallLabel.getLabel()
                    + "' requires " + lastCallLabel.getRank()
                    + " parameters instead of " + lastCallParameters.size() + ".");
        }

        TIntArrayList attachedNodes = new TIntArrayList();
        for(String parameter : lastCallParameters) {
            int nodeId = variableToNodeId.get(parameter);
            attachedNodes.add(nodeId);
        }

        heapBuilder.addNonterminalEdge(lastCallLabel, attachedNodes);

        lastCallLabel = null;
        lastCallParameters = null;
    }

    /**
     * Adds a parameter to the list of parameters of the currently considered predicate call.
     */
    @Override
    public void exitParameter(SeparationLogicParser.ParameterContext ctx) {

        if(lastVariableName == null) {
            throw new UnsupportedOperationException("At the moment, " +
                    "'null' has to be modeled as a dedicated free variable.");
        }

        lastCallParameters.add(lastVariableName);
    }

    /**
     * Treats predicate symbols both on the left-hand side of rules
     * and in the form of predicate calls.
     */
    @Override
    public void enterPredicateSymbol(SeparationLogicParser.PredicateSymbolContext ctx) {

        String label = ctx.getText();
        lastCallLabel = scene.getNonterminal(label);

        // this predicate determines the left-hand side of grammar rules
        if(lhs == null) {
            lhs = lastCallLabel;
            lastCallLabel = null;
        }
    }
}
