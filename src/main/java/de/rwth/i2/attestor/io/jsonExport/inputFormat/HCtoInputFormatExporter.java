package de.rwth.i2.attestor.io.jsonExport.inputFormat;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import gnu.trove.list.array.TIntArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HCtoInputFormatExporter {

    /**
     * Transformes the given HC into a jsonObject in the input format.
     * When written to a file, this can be used to generate costum inputs
     * from the previous run.
     *
     * @param hc the heapconfiguration to transform
     * @return an JSONObject representing the hc
     */
    public static JSONObject getInInputFormat(HeapConfiguration hc) {

        JSONObject res = new JSONObject();
        res.put("nodes", new JSONArray());
        res.put("externals", new JSONArray());
        res.put("variables", new JSONArray());
        res.put("selectors", new JSONArray());
        res.put("hyperedges", new JSONArray());

        Map<Integer, Integer> idMapping = addNodesOfTo(hc, res);
        addVariablesOfTo(hc, res, idMapping);
        addSelectorsOfTo(hc, res, idMapping);
        addHyperedgesIfTo(hc, res, idMapping);

        return res;
    }


    /**
     * Creates JSONObjects of the form <br>
     * "type"=NodeType,<br>
     * "number"=1<br>
     * for each node of the heap and appends it to res.
     * Furthermore appends the corresponding index to the list of external nodes
     * if appropriate.
     *
     * @param hc
     * @param res
     * @return
     */
    private static Map<Integer, Integer> addNodesOfTo(HeapConfiguration hc, JSONObject res) {

        Map<Integer, Integer> idMapping = new LinkedHashMap<>();

        TIntArrayList nodes = hc.nodes();
        for (int i = 0; i < nodes.size(); i++) {
            String nodeType = hc.nodeTypeOf(nodes.get(i)).toString();

            JSONObject nodeObject = new JSONObject();
            nodeObject.put("type", nodeType);
            nodeObject.put("number", 1);

            res.append("nodes", nodeObject);

            if (hc.isExternalNode(nodes.get(i))) {
                res.append("externals", i);
            }

            idMapping.put(nodes.get(i), i);
        }
        return idMapping;
    }

    private static void addVariablesOfTo(HeapConfiguration hc, JSONObject res, Map<Integer, Integer> idMapping) {

        TIntArrayList varEdges = hc.variableEdges();
        for (int i = 0; i < varEdges.size(); i++) {
            int varEdge = varEdges.get(i);

            JSONObject variableObject = new JSONObject();
            variableObject.put("name", hc.nameOf(varEdge));
            int targetInHc = hc.targetOf(varEdge);
            int targetInJson = idMapping.get(targetInHc);
            variableObject.put("target", targetInJson);

            res.append("variables", variableObject);
        }

    }

    private static void addSelectorsOfTo(HeapConfiguration hc, JSONObject res, Map<Integer, Integer> idMapping) {

        TIntArrayList nodes = hc.nodes();
        for (int s = 0; s < nodes.size(); s++) {
            int sourceInHc = nodes.get(s);
            TIntArrayList successors = hc.successorNodesOf(sourceInHc);
            List<SelectorLabel> selectorLabels = hc.selectorLabelsOf(sourceInHc);
            for (int t = 0; t < successors.size(); t++) {
                int targetInHc = successors.get(t);
                SelectorLabel sel = selectorLabels.get(t);

                JSONObject selectorObject = new JSONObject();
                selectorObject.put("label", sel.getLabel());
                if (sel instanceof AnnotatedSelectorLabel) {
                    AnnotatedSelectorLabel annotatedSel = (AnnotatedSelectorLabel) sel;
                    selectorObject.put("annotation", annotatedSel.getAnnotation());
                }
                selectorObject.put("origin", idMapping.get(sourceInHc));
                selectorObject.put("target", idMapping.get(targetInHc));

                res.append("selectors", selectorObject);
            }
        }

    }

    private static void addHyperedgesIfTo(HeapConfiguration hc, JSONObject res, Map<Integer, Integer> idMapping) {

        TIntArrayList hyperedges = hc.nonterminalEdges();
        for (int i = 0; i < hyperedges.size(); i++) {
            int hyperedge = hyperedges.get(i);
            Nonterminal nt = hc.labelOf(hyperedge);
            JSONObject hyperedgeObject = new JSONObject();
            hyperedgeObject.put("label", nt.getLabel());
            if (nt instanceof IndexedNonterminal) {
                hyperedgeObject.put("index", getJsonIndex(nt));
            }
            TIntArrayList tentacles = hc.attachedNodesOf(hyperedge);
            JSONArray tentacleArray = new JSONArray();
            for (int t = 0; t < tentacles.size(); t++) {
                Object tentacleInHc = tentacles.get(t);
                Integer tentacleInJson = idMapping.get(tentacleInHc);
                tentacleArray.put(tentacleInJson);
            }
            hyperedgeObject.put("tentacles", tentacleArray);

            res.append("hyperedges", hyperedgeObject);

        }

    }


    private static JSONArray getJsonIndex(Nonterminal nt) {

        JSONArray indexArray = new JSONArray();

        IndexedNonterminal indexedNt = (IndexedNonterminal) nt;
        Index index = indexedNt.getIndex();

        for (int i = 0; i < index.size(); i++) {
            IndexSymbol symb = index.get(i);
            if (symb instanceof ConcreteIndexSymbol) {
                if (symb.isBottom()) {
                    indexArray.put(symb.toString().toUpperCase());
                } else {
                    indexArray.put(symb.toString().toLowerCase());
                }
            } else if (symb instanceof AbstractIndexSymbol) {
                indexArray.put("_" + symb.toString());
            } else if (symb instanceof IndexVariable) {
                indexArray.put("()");
            }
        }
        return indexArray;
    }


}
