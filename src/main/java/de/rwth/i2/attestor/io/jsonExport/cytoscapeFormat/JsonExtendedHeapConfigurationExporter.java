package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import gnu.trove.iterator.TIntIterator;
import org.json.JSONStringer;
import org.json.JSONWriter;

import java.io.Writer;

/**
 * Created by christina on 22.08.17.
 *
 * Exports heap configurations with external nodes to json format, marking the external nodes.
 */
public class JsonExtendedHeapConfigurationExporter extends JsonHeapConfigurationExporter {

    public JsonExtendedHeapConfigurationExporter(Writer writer) {

        super(writer);
    }

    public JsonExtendedHeapConfigurationExporter() {

    }

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

    private void writeNodesAndEdges(JSONWriter jsonWriter, HeapConfiguration heapConfiguration){

        writeNodes(jsonWriter, heapConfiguration);
        super.writeNonterminalHyperedges(jsonWriter, heapConfiguration);
        super.writeVariables(jsonWriter, heapConfiguration);

        jsonWriter.endArray()
                .key("edges")
                .array();

        super.writeSelectors(jsonWriter, heapConfiguration);
        super.writeNonterminalTentacles(jsonWriter, heapConfiguration);
        super.writeVariableTentacles(jsonWriter, heapConfiguration);

    }


    private void writeNodes(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iterator = heapConfiguration.nodes().iterator();
        while (iterator.hasNext()) {
            int node = iterator.next();
            boolean isExternal = heapConfiguration.isExternalNode(node);
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(node);
            if (isExternal) {
                jsonWriter.key("type").value("externalNode");
            } else {
                jsonWriter.key("type").value("node");
            }
            String nodeInfo = Integer.toString(node);
            if (isExternal) {
                nodeInfo += ", ext " + heapConfiguration.externalIndexOf(node);
            }
            jsonWriter.key("label").value(nodeInfo);
            jsonWriter.key("nodeType").value(heapConfiguration.nodeTypeOf(node).toString());
            jsonWriter.endObject().endObject();
        }
    }
}
