package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.list.array.TIntArrayList;
import org.json.JSONStringer;
import org.json.JSONWriter;

import java.io.Writer;

public class JsonHeapConfigurationExporter implements HeapConfigurationExporter {

    protected Writer writer;

    public JsonHeapConfigurationExporter(Writer writer) {

        this.writer = writer;
    }

    public JsonHeapConfigurationExporter() {

        this.writer = null;
    }

    @Override
    public void export(HeapConfiguration heapConfiguration) {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                .key("elements")
                .object()
                .key("nodes")
                .array();

        writeNodesAndEdges(jsonWriter, heapConfiguration);

        jsonWriter.endArray()
                .endObject()
                .endObject();

    }

    @Override
    public String exportForReport(HeapConfiguration heapConfiguration) {

        JSONStringer jsonStringer = new JSONStringer();

        jsonStringer.object()
                .key("nodes")
                .array();

        writeNodesAndEdges(jsonStringer, heapConfiguration);

        jsonStringer.endArray()
                .endObject();

        return jsonStringer.toString();

    }

    private void writeNodesAndEdges(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        writeNodes(jsonWriter, heapConfiguration);
        writeNonterminalHyperedges(jsonWriter, heapConfiguration);
        writeVariables(jsonWriter, heapConfiguration);

        jsonWriter.endArray()
                .key("edges")
                .array();

        writeSelectors(jsonWriter, heapConfiguration);
        writeNonterminalTentacles(jsonWriter, heapConfiguration);
        writeVariableTentacles(jsonWriter, heapConfiguration);
    }

    private void writeNodes(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.nodes().iterator();
        while (iterator.hasNext()) {
            int node = iterator.next();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(node);
            jsonWriter.key("type").value("node");
            jsonWriter.key("nodeType").value(heapConfiguration.nodeTypeOf(node).toString());
            jsonWriter.endObject().endObject();
        }
    }

    public void writeNonterminalHyperedges(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.nonterminalEdges().iterator();
        while (iterator.hasNext()) {
            int edge = iterator.next();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(edge);
            jsonWriter.key("type").value("hyperedge");
            jsonWriter.key("label").value(heapConfiguration.labelOf(edge).toString());
            jsonWriter.key("annotation").value(heapConfiguration.labelOf(edge).toString());
            jsonWriter.endObject().endObject();
        }
    }

    public void writeVariables(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.variableEdges().iterator();
        while (iterator.hasNext()) {
            int variable = iterator.next();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(variable);
            jsonWriter.key("type").value("variable");
            jsonWriter.key("label").value(heapConfiguration.nameOf(variable));
            jsonWriter.endObject().endObject();
        }
    }

    public void writeSelectors(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.nodes().iterator();
        while (iterator.hasNext()) {
            int source = iterator.next();
            for (SelectorLabel sel : heapConfiguration.selectorLabelsOf(source)) {
                int target = heapConfiguration.selectorTargetOf(source, sel);
                writeEdge(jsonWriter, source, target, sel.toString(), "selector");
            }
        }
    }

    public void writeNonterminalTentacles(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.nonterminalEdges().iterator();
        while (iterator.hasNext()) {
            int source = iterator.next();
            TIntArrayList att = heapConfiguration.attachedNodesOf(source);
            for (int i = 0; i < att.size(); i++) {
                int target = att.get(i);
                String label = String.valueOf(i);
                writeEdge(jsonWriter, source, target, label, "tentacle");
            }
        }
    }

    public void writeVariableTentacles(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.variableEdges().iterator();
        while (iterator.hasNext()) {
            int source = iterator.next();
            int target = heapConfiguration.targetOf(source);
            writeEdge(jsonWriter, source, target, "", "variable");
        }
    }

    private void writeEdge(JSONWriter jsonWriter, int source, int target, String label, String type) {

        jsonWriter.object()
                .key("data")
                .object()
                .key("source").value(source)
                .key("target").value(target)
                .key("label").value(label)
                .key("type").value(type)
                .endObject()
                .endObject();
    }
}