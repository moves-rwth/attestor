package de.rwth.i2.attestor.io.jsonExport;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.strategies.indexedGrammarStrategies.IndexedNonterminal;
import gnu.trove.iterator.TIntIterator;
import org.json.JSONWriter;

import java.io.Writer;

/**
 * Created by christina on 22.08.17.
 */
public class JsonExtendedHeapConfigurationExporter extends JsonHeapConfigurationExporter {

    public JsonExtendedHeapConfigurationExporter(Writer writer) {
        super(writer);
    }

    public void export(HeapConfiguration heapConfiguration){

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                .key("elements")
                .object()
                .key("nodes")
                .array();

        writeNodes(jsonWriter, heapConfiguration);
        writeNonterminalHyperedges(jsonWriter, heapConfiguration);
        super.writeVariables(jsonWriter, heapConfiguration);

        jsonWriter.endArray()
                .key("edges")
                .array();

        super.writeSelectors(jsonWriter, heapConfiguration);
        super.writeNonterminalTentacles(jsonWriter, heapConfiguration);
        super.writeVariableTentacles(jsonWriter, heapConfiguration);

        jsonWriter.endArray()
                .endObject()
                .endObject();

    }


    private void writeNodes(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iter = heapConfiguration.nodes().iterator();
        while(iter.hasNext()) {
            int node = iter.next();
            boolean isExternal = heapConfiguration.isExternalNode(node);
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(node);
            if(isExternal){
                jsonWriter.key("type").value("externalNode");
            } else {
                jsonWriter.key("type").value("node");
            }
            String nodeInfo = Integer.toString(node);
            if(isExternal){
                nodeInfo += ", ext " + heapConfiguration.externalIndexOf(node);
            }
            jsonWriter.key("label").value(nodeInfo);
            jsonWriter.key("nodeType").value( heapConfiguration.nodeTypeOf(node).toString() );
            jsonWriter.endObject().endObject();
        }
    }

    public void writeNonterminalHyperedges(JSONWriter jsonWriter, HeapConfiguration heapConfiguration) {

        TIntIterator iter = heapConfiguration.nonterminalEdges().iterator();
        while(iter.hasNext()) {
            int edge= iter.next();
            jsonWriter.object().key("data").object();
            jsonWriter.key("id").value(edge);
            jsonWriter.key("type").value("hyperedge");
            jsonWriter.key("label").value( heapConfiguration.labelOf(edge).toString());
            jsonWriter.key("annotation").value( heapConfiguration.labelOf(edge).toString() );
            jsonWriter.endObject().endObject();
        }
    }
}
