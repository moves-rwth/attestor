package de.rwth.i2.attestor.io.hcToJava;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class HcToJava {

    private final static String VAR_PREFIX = "x";
    private final static String SELECTOR_SETTER_PREFIX = "set";

    private HeapConfiguration heapConfiguration;
    private TIntObjectMap<String> nodeToExpression;
    private OutputStreamWriter writer;
    private final String indentPrefix;

    public HcToJava(HeapConfiguration heapConfiguration, OutputStreamWriter writer, String indentPrefix) {

        assert(heapConfiguration.countNonterminalEdges() == 0);

        this.heapConfiguration = heapConfiguration;
        this.nodeToExpression = new TIntObjectHashMap<>();
        this.writer = writer;
        this.indentPrefix = indentPrefix;


    }

    public void declareVariables(String modifier) throws IOException {

        TIntIterator iter = heapConfiguration.variableEdges().iterator();
        while(iter.hasNext()) {
            int var = iter.next();
            declareVariable(var, modifier);
        }
    }

    public void translateHc() throws IOException {

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            translateNode(node);
        }

        iter = heapConfiguration.nodes().iterator();
        while (iter.hasNext()) {
            int node = iter.next();
            List<SelectorLabel> selectorLabels = heapConfiguration.selectorLabelsOf(node);
            for(SelectorLabel sel : selectorLabels) {
                int target = heapConfiguration.selectorTargetOf(node, sel);
                translateSelector(node, sel, target);
            }
        }

        iter = heapConfiguration.variableEdges().iterator();
        while(iter.hasNext()) {
            int var = iter.next();
            defineVariable(var);
        }
    }

    private void translateNode(int node) throws IOException {

        Type type = heapConfiguration.nodeTypeOf(node);

        if(hasAttachedConstants(node)) {
            return;
        }

        String varName = VAR_PREFIX + node;
        nodeToExpression.put(node, varName);
        String builder = indentPrefix +
                type +
                " " +
                varName +
                " = new " +
                type +
                "();" +
                System.lineSeparator();

        writer.write(builder);
    }

    private boolean hasAttachedConstants(int node) {

        TIntIterator attVarIter = heapConfiguration.attachedVariablesOf(node).iterator();
        while(attVarIter.hasNext()) {
            int varEdge = attVarIter.next();
            String varName = heapConfiguration.nameOf(varEdge);
            if(Constants.isConstant(varName)) {
                nodeToExpression.put(node, varName);
                return true;
            }
        }
        return false;
    }

    private void translateSelector(int from, SelectorLabel sel, int to) throws IOException {

        StringBuilder builder = new StringBuilder();
        String selName = sel.toString();
        builder.append(indentPrefix)
                .append(nodeToExpression.get(from))
                .append(".")
                .append(SELECTOR_SETTER_PREFIX)
                .append(selName.substring(0, 1).toUpperCase())
                .append(selName.substring(1))
                .append("(")
                .append(nodeToExpression.get(to))
                .append(");")
                .append(System.lineSeparator());

        writer.write(builder.toString());
    }

    private void declareVariable(int var, String modifier) throws IOException {

        String name = heapConfiguration.nameOf(var);

        if(Constants.isConstant(name)) {
            return;
        }

        int targetNode = heapConfiguration.targetOf(var);
        Type type = heapConfiguration.nodeTypeOf(targetNode);

        String builder = indentPrefix +
                modifier +
                " " +
                type +
                " " +
                name +
                ";" +
                System.lineSeparator();

        writer.write(builder);
    }

    private void defineVariable(int var) throws IOException {

        String name = heapConfiguration.nameOf(var);

        if(Constants.isConstant(name)) {
            return;
        }

        int targetNode = heapConfiguration.targetOf(var);

        String builder = indentPrefix +
                name +
                " = " +
                nodeToExpression.get(targetNode) +
                ";" +
                System.lineSeparator();

        writer.write(builder);
    }

}
