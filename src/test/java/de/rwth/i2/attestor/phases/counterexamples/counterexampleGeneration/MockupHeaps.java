package de.rwth.i2.attestor.phases.counterexamples.counterexampleGeneration;

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

    private SelectorLabel sel;
    private SelectorLabel selB;
    private Type type;
    private HeapConfiguration emptyHeap;
    private TIntArrayList constantExternals;

    protected MockupHeaps(SceneObject sceneObject) {
        super(sceneObject);

        sel = sceneObject.scene().getSelectorLabel("mockupSel");
        selB = sceneObject.scene().getSelectorLabel("mockupB");
        type = sceneObject.scene().getType("mockupType");
        type.addSelectorLabel(sel, TypeNames.NULL);
        type.addSelectorLabel(selB, TypeNames.NULL);

        emptyHeap = sceneObject.scene()
                .createProgramState(sceneObject.scene().createHeapConfiguration())
                .getHeap();

        constantExternals = emptyHeap.nodes();

        setupHeap();
        setupHeapInScope();
        setupHeapOutsideScope();
        setupPostcondition();
    }

    private void setupHeap() {

        TIntArrayList nodes = new TIntArrayList();
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
    }

    private void setupHeapInScope() {

        TIntArrayList nodes = heap.nodes();
        int node = nodes.get(constantExternals.size());
        HeapConfigurationBuilder builder = heap.clone()
                .builder()
                .removeSelector(node, sel)
                .removeIsolatedNode(node);
        for(int i=0; i < constantExternals.size(); i++) {
            builder.setExternal(constantExternals.get(i));
        }
        heapInScope = builder
                .setExternal(node+1)
                .build();
    }

    private void setupHeapOutsideScope() {

        TIntArrayList outsideScopeNodes = new TIntArrayList();
        Nonterminal placeholder = scene()
                .createNonterminal("MockupPlaceholder", 1+constantExternals.size(),
                        new boolean[1+constantExternals.size()]);
        HeapConfigurationBuilder builder = emptyHeap.clone()
                .builder()
                .addNodes(type, 2, outsideScopeNodes)
                .addSelector(outsideScopeNodes.get(0), sel, outsideScopeNodes.get(1));

        TIntArrayList tentacles = new TIntArrayList();
        tentacles.addAll(constantExternals);
        tentacles.add(outsideScopeNodes.get(1));

        heapOutsideScope = builder
                .addNonterminalEdge(placeholder, tentacles)
                .build();

        TIntArrayList variables = heapOutsideScope.variableEdges();
        builder = heapOutsideScope.builder();
        for(int i=0; i < variables.size(); i++) {
            builder.removeVariableEdge(variables.get(i));
        }
        heapOutsideScope = builder.build();
        placeholderEdge = heapOutsideScope.nonterminalEdges().get(0);
    }

    private void setupPostcondition() {

        TIntArrayList postconditionNodes = new TIntArrayList();
        HeapConfigurationBuilder builder = emptyHeap.clone()
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
