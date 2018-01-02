package de.rwth.i2.attestor.counterexamples;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeNames;
import gnu.trove.list.array.TIntArrayList;

public class MockupHeaps extends SceneObject {

    private HeapConfiguration heap;
    private HeapConfiguration heapInScope;
    private HeapConfiguration heapOutsideScope;
    private HeapConfiguration postcondition;
    private int placeholderEdge;

    protected MockupHeaps(SceneObject sceneObject) {
        super(sceneObject);

        TIntArrayList nodes = new TIntArrayList();
        SelectorLabel sel = sceneObject.scene().getSelectorLabel("mockupSel");
        SelectorLabel selB = sceneObject.scene().getSelectorLabel("mockupB");
        Type type = sceneObject.scene().getType("mockupType");
        type.addSelectorLabel(sel, TypeNames.NULL);
        type.addSelectorLabel(selB, TypeNames.NULL);

        HeapConfiguration emptyHeap = sceneObject.scene().createProgramState(
                sceneObject.scene().createHeapConfiguration()
        ).getHeap();

        TIntArrayList constantExternals = emptyHeap.nodes();

        heap = emptyHeap.clone()
                .builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addSelector(nodes.get(2), sel, nodes.get(3))
                .addSelector(nodes.get(2), selB, nodes.get(1))
                .addSelector(nodes.get(3), sel, nodes.get(4))
                .addVariableEdge("mockupVar", nodes.get(2))
                .build();

        HeapConfigurationBuilder builder = heap.clone()
                .builder()
                .removeSelector(nodes.get(0), sel)
                .removeIsolatedNode(nodes.get(0));
        for(int i=0; i < constantExternals.size(); i++) {
            builder.setExternal(constantExternals.get(i));
        }
        heapInScope = builder
                .setExternal(nodes.get(1))
                .build();


        TIntArrayList outsideScopeNodes = new TIntArrayList();
        Nonterminal placeholder = sceneObject.scene()
                .createNonterminal("MockupPlaceholder", 1+constantExternals.size(),
                        new boolean[1+constantExternals.size()]);
        builder = emptyHeap.clone()
                .builder()
                .addNodes(type, 2, outsideScopeNodes)
                .addSelector(outsideScopeNodes.get(0), sel, outsideScopeNodes.get(1));

        TIntArrayList tentacles = new TIntArrayList();
        tentacles.addAll(constantExternals);
        tentacles.add(outsideScopeNodes.get(1));

        heapOutsideScope = builder
                .addNonterminalEdge(placeholder, tentacles)
                .build();
        placeholderEdge = heapOutsideScope.nonterminalEdges().get(0);

        TIntArrayList postconditionNodes = new TIntArrayList();
        builder = emptyHeap.clone()
                .builder()
                .addNodes(type, 3, postconditionNodes)
                .addSelector(postconditionNodes.get(0), selB, postconditionNodes.get(1))
                .addSelector(postconditionNodes.get(1), selB, postconditionNodes.get(2))
                .addVariableEdge("mockupVar", postconditionNodes.get(2));
        for(int i=0; i < constantExternals.size(); i++) {
            builder.setExternal(constantExternals.get(i));
        }
        postcondition = builder
                .setExternal(postconditionNodes.get(0))
                .build();
    }

    public HeapConfiguration getHeap() {
        return heap.clone();
    }

    public HeapConfiguration getHeapInScope() {
        return heapInScope.clone();
    }

    public HeapConfiguration getHeapOutsideScope() {
        return heapOutsideScope.clone();
    }

    public int getPlaceholderEdge() {
        return placeholderEdge;
    }

    public HeapConfiguration getPostcondition() {
        return postcondition;
    }



}
