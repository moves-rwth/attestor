package de.rwth.i2.attestor.graph.heap.internal;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

public final class ExampleHcImplFactory extends SceneObject {

    public ExampleHcImplFactory(SceneObject otherObject) {

        super(otherObject);
    }

    public HeapConfiguration getEmptyHc() {

        return new InternalHeapConfiguration();
    }

    public HeapConfiguration getSimpleDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel sel = scene().getSelectorLabel("next");
        Nonterminal nt = scene().createNonterminal("3", 3, new boolean[]{false, false, false});
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addSelector(nodes.get(1), sel, nodes.get(2))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(2)}))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getBadTwoElementDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(0))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build();
    }

    public HeapConfiguration getTwoElementDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build();
    }

    public HeapConfiguration getThreeElementDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        type.addSelectorLabel(next, Constants.NULL);
        type.addSelectorLabel(prev, Constants.NULL);
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), prev, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getThreeElementDLLWithConstants() {

        HeapConfiguration result = getEmptyGraphWithConstants();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        type.addSelectorLabel(next, Constants.NULL);
        type.addSelectorLabel(prev, Constants.NULL);
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), prev, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getFiveElementDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 5, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), prev, nodes.get(1))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .addSelector(nodes.get(3), prev, nodes.get(2))
                .addSelector(nodes.get(3), next, nodes.get(4))
                .addSelector(nodes.get(4), prev, nodes.get(3))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getBrokenFourElementDLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), prev, nodes.get(1))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .addSelector(nodes.get(3), prev, nodes.get(2))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getTLLRule() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        Type type = scene().getType("node");
        type.addSelectorLabel(left, Constants.NULL);
        type.addSelectorLabel(right, Constants.NULL);
        type.addSelectorLabel(parent, Constants.NULL);

        Nonterminal nT = scene().createNonterminal("TLL", 4, new boolean[]{false, false, false, false});

        return result.builder()
                .addNodes(type, 7, nodes)
                .addSelector(nodes.get(0), left, nodes.get(1))
                .addSelector(nodes.get(0), right, nodes.get(2))
                .addSelector(nodes.get(0), parent, nodes.get(3))
                .addVariableEdge("XYZ", nodes.get(3))
                .addVariableEdge("ZYX", nodes.get(0))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(5), nodes.get(4)}))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(5), nodes.get(6)}))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .setExternal(nodes.get(6))
                .build();
    }

    public HeapConfiguration getTLLRulePermuted() {

        HeapConfiguration result = new InternalHeapConfiguration();
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        Nonterminal nT = scene().createNonterminal("Tree", 4, new boolean[]{false, false, false, false});

        return result.builder()
                .addNodes(type, 7, nodes)
                .addSelector(nodes.get(1), left, nodes.get(0))
                .addSelector(nodes.get(1), right, nodes.get(2))
                .addSelector(nodes.get(1), parent, nodes.get(3))
                .addVariableEdge("XYZ", nodes.get(3))
                .addVariableEdge("ZYX", nodes.get(1))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(5), nodes.get(4)}))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(1), nodes.get(5), nodes.get(6)}))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .setExternal(nodes.get(6))
                .build();
    }

    public HeapConfiguration getTree() {

        HeapConfiguration result = new InternalHeapConfiguration();
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), left, nodes.get(1))
                .addSelector(nodes.get(0), right, nodes.get(2))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getLargerTree() {

        HeapConfiguration result = new InternalHeapConfiguration();
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        return result.builder()
                .addNodes(type, 6, nodes)
                .addSelector(nodes.get(1), parent, nodes.get(0))
                .addSelector(nodes.get(1), left, nodes.get(2))
                .addSelector(nodes.get(1), right, nodes.get(3))
                .addSelector(nodes.get(2), left, nodes.get(4))
                .addSelector(nodes.get(2), right, nodes.get(5))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(5))
                .build();
    }

    public HeapConfiguration getLargerTreeWithOutExternals() {

        HeapConfiguration result = new InternalHeapConfiguration();
        Type type = scene().getType("node");
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        return result.builder()
                .addNodes(type, 6, nodes)
                .addSelector(nodes.get(1), parent, nodes.get(0))
                .addSelector(nodes.get(1), left, nodes.get(2))
                .addSelector(nodes.get(1), right, nodes.get(3))
                .addSelector(nodes.get(2), left, nodes.get(4))
                .addSelector(nodes.get(2), right, nodes.get(5))
                .build();
    }

    public HeapConfiguration getListAndConstants() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel next = scene().getSelectorLabel("next");

        Type type = scene().getType("List");
        type.addSelectorLabel(next, Constants.NULL);

        Type intType = scene().getType("int");


        return result.builder()
                .addNodes(intType, 2, nodes)
                .addNodes(type, 4, nodes)
                .addVariableEdge(Constants.ONE, nodes.get(1))
                .addVariableEdge(Constants.TRUE, nodes.get(1))
                .addVariableEdge(Constants.FALSE, nodes.get(0))
                .addVariableEdge(Constants.ZERO, nodes.get(0))
                .addVariableEdge(Constants.NULL, nodes.get(5))
                .addVariableEdge("y", nodes.get(2))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .addSelector(nodes.get(3), next, nodes.get(4))
                .addSelector(nodes.get(4), next, nodes.get(5))
                .build();
    }

    public HeapConfiguration getEmptyGraphWithConstants() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("node");
        Type booleanType = scene().getType("int");

        return result.builder()
                .addNodes(booleanType, 2, nodes)
                .addNodes(type, 1, nodes)
                .addVariableEdge(Constants.ONE, nodes.get(1))
                .addVariableEdge(Constants.TRUE, nodes.get(1))
                .addVariableEdge(Constants.FALSE, nodes.get(0))
                .addVariableEdge(Constants.ZERO, nodes.get(0))
                .addVariableEdge(Constants.NULL, nodes.get(2))
                .build();
    }

    public HeapConfiguration getList() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel next = scene().getSelectorLabel("next");
        Type type = scene().getType("List");
        type.addSelectorLabel(next, Constants.NULL);


        return result.builder()
                .addNodes(type, 4, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(3))
                .addVariableEdge("x", nodes.get(0))
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .build();
    }

    public HeapConfiguration getCyclicList() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        Nonterminal nt = scene().createNonterminal("List", 2, new boolean[]{false, true});

        return result.builder()
                .addNodes(type, 2, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }

    public HeapConfiguration getAbstractCyclicList() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        Nonterminal nt = scene().createNonterminal("List", 2, new boolean[]{false, true});

        return result.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }

    public HeapConfiguration getAbstractList() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        Nonterminal nt = scene().createNonterminal("SLL", 2, new boolean[]{false, true});
        SelectorLabel next = scene().getSelectorLabel("next");

        return result.builder()
                .addNodes(type, 2, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(1))
                .addVariableEdge("x", nodes.get(0))
                .addNonterminalEdge(nt, nodes)
                .build();
    }


    public HeapConfiguration getListAndConstantsWithChange() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        SelectorLabel next = scene().getSelectorLabel("next");

        return result.builder()
                .addNodes(type, 4, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(3))
                .addVariableEdge("y", nodes.get(0))
                .addSelector(nodes.get(0), next, nodes.get(0))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(2), next, nodes.get(3))
                .build();
    }

    public HeapConfiguration expectedResultEasyList() {

        HeapConfiguration empty = getEmptyGraphWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");

        SelectorLabel nextSel = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(2), nextSel, nullNode)
                .addSelector(nodes.get(1), nextSel, nodes.get(2))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(2), valSelector, trueNode)
                .build();

    }

    public HeapConfiguration expectedResultEasyList_beforeReturn() {

        HeapConfiguration empty = getEmptyGraphWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");

        SelectorLabel nextSel = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(2), nextSel, nullNode)
                .addSelector(nodes.get(1), nextSel, nodes.get(2))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(2), valSelector, trueNode)
                .addVariableEdge("$r4", nodes.get(2))
                .addVariableEdge("$r6", nodes.get(0))
                .addVariableEdge("$r5", nodes.get(1))
                .addVariableEdge("r8", nodes.get(2))
                .addVariableEdge("$r7", nullNode)
                .build();

    }

    public HeapConfiguration expectedResNormalList() {

        HeapConfiguration empty = getEmptyGraphWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");

        SelectorLabel nextSel = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(0), nextSel, nullNode)
                .addSelector(nodes.get(0), valSelector, trueNode)
                .addSelector(nodes.get(1), nextSel, nodes.get(0))
                .addSelector(nodes.get(2), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration expectedResBoolList() {

        HeapConfiguration empty = getEmptyGraphWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.BoolList");

        SelectorLabel nextSelector = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(0), nextSelector, nullNode)
                .addSelector(nodes.get(0), valSelector, trueNode)
                .addSelector(nodes.get(1), nextSelector, nodes.get(0))
                .addSelector(nodes.get(1), valSelector, trueNode)
                .addSelector(nodes.get(2), nextSelector, nodes.get(1))
                .addSelector(nodes.get(2), valSelector, trueNode)
                .build();
    }


    public HeapConfiguration expectedResNormalList_beforeReturn() {

        HeapConfiguration empty = getEmptyGraphWithConstants();

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
        SelectorLabel nextSelector = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));
        int falseNode = empty.targetOf(empty.variableWith(Constants.FALSE));

        TIntArrayList nodes = new TIntArrayList();

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(0), nextSelector, nullNode)
                .addSelector(nodes.get(0), valSelector, trueNode)
                .addSelector(nodes.get(1), nextSelector, nodes.get(0))
                .addSelector(nodes.get(2), nextSelector, nodes.get(1))
                .addVariableEdge("$r4", nodes.get(0))
                .addVariableEdge("$r6", nodes.get(2))
                .addVariableEdge("$r5", nodes.get(1))
                .addVariableEdge("r7", nodes.get(0))
                .addVariableEdge("$z0", falseNode)
                .build();
    }

    public HeapConfiguration expectedResStaticList() {

        HeapConfiguration empty = getEmptyGraphWithConstants();

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.EasyList");
        SelectorLabel nextSelector = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));
        int falseNode = empty.targetOf(empty.variableWith(Constants.FALSE));

        TIntArrayList nodes = new TIntArrayList();

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(0), nextSelector, nullNode)
                .addSelector(nodes.get(0), valSelector, falseNode)
                .addSelector(nodes.get(1), nextSelector, nodes.get(0))
                .addSelector(nodes.get(1), valSelector, trueNode)
                .addSelector(nodes.get(2), nextSelector, nodes.get(1))
                .build();
    }

    public HeapConfiguration expectedResStaticList_beforeReturn() {

        HeapConfiguration empty = getEmptyGraphWithConstants();

        Type defaultType = scene().getType("de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.NormalList");
        SelectorLabel nextSelector = scene().getSelectorLabel("next");
        SelectorLabel valSelector = scene().getSelectorLabel("value");

        int nullNode = empty.targetOf(empty.variableWith(Constants.NULL));
        int trueNode = empty.targetOf(empty.variableWith(Constants.TRUE));
        int falseNode = empty.targetOf(empty.variableWith(Constants.FALSE));

        TIntArrayList nodes = new TIntArrayList();

        return empty.builder()
                .addNodes(defaultType, 3, nodes)
                .addSelector(nodes.get(0), nextSelector, nullNode)
                .addSelector(nodes.get(0), valSelector, falseNode)
                .addSelector(nodes.get(1), nextSelector, nodes.get(0))
                .addSelector(nodes.get(1), valSelector, trueNode)
                .addSelector(nodes.get(2), nextSelector, nodes.get(1))
                .addVariableEdge("$z0", falseNode)
                .addVariableEdge("r3", nodes.get(2))
                .addVariableEdge("r4", nodes.get(0))
                .addVariableEdge("r1", nodes.get(0))
                .addVariableEdge("r2", nodes.get(0))
                .build();
    }

    public HeapConfiguration getListRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getListRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addNonterminalEdge(listLabel, new TIntArrayList((new int[]{nodes.get(1), nodes.get(2)})))
                .build();
    }

    public HeapConfiguration getListRule3() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getListRule2Test() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addVariableEdge("x", nodes.get(2))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getListRule2TestFail() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addVariableEdge("x", nodes.get(1))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getTestForListRule3() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 6, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(4)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(4), nodes.get(5)}))
                .addVariableEdge("y", nodes.get(5))
                .build();
    }

    public HeapConfiguration getTestForListRule3Fail() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .addVariableEdge("y", nodes.get(1))
                .build();
    }

    public HeapConfiguration getDLLRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        SelectorLabel prevSel = scene().getSelectorLabel("p");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(1), prevSel, nodes.get(0))
                .build();
    }

    public HeapConfiguration getDLLRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        SelectorLabel prevSel = scene().getSelectorLabel("p");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, false});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(1), prevSel, nodes.get(0))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration get4DLLRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        SelectorLabel prevSel = scene().getSelectorLabel("p");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 5, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addSelector(nodes.get(2), prevSel, nodes.get(1))
                .addSelector(nodes.get(2), nextSel, nodes.get(3))
                .addSelector(nodes.get(3), prevSel, nodes.get(2))
                .addSelector(nodes.get(3), nextSel, nodes.get(4))
                .build();
    }

    public HeapConfiguration get4DLLRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        SelectorLabel prevSel = scene().getSelectorLabel("p");
        Nonterminal listLabel = scene().createNonterminal("DLL4", 4, new boolean[]{false, false});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 5, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addSelector(nodes.get(2), prevSel, nodes.get(1))
                .addSelector(nodes.get(2), nextSel, nodes.get(0))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(3), nodes.get(4)}))
                .build();
    }

    public HeapConfiguration get4DLLRule3() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        SelectorLabel prevSel = scene().getSelectorLabel("p");
        Nonterminal listLabel = scene().createNonterminal("DLL4", 4, new boolean[]{false, false});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 5, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .addSelector(nodes.get(3), prevSel, nodes.get(0))
                .addSelector(nodes.get(3), nextSel, nodes.get(4))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2), nodes.get(0), nodes.get(3)}))
                .build();
    }

    public HeapConfiguration get4DLLRule4() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("DLL");
        Nonterminal listLabel = scene().createNonterminal("DLL4", 4, new boolean[]{false, false});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 6, nodes)
                .setExternal(nodes.get(2))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .setExternal(nodes.get(5))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3), nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(4), nodes.get(5)}))
                .build();
    }

    public HeapConfiguration getAdmissibleGraph() {

        HeapConfiguration graph = getListRule2();

        int ntEdge = graph.nonterminalEdges().get(0);
        int node = graph.attachedNodesOf(ntEdge).get(1);

        return graph.builder()
                .addVariableEdge("x", node)
                .build();
    }

    public HeapConfiguration getInAdmissibleGraph() {

        HeapConfiguration graph = getListRule2();

        int ntEdge = graph.nonterminalEdges().get(0);
        int node = graph.attachedNodesOf(ntEdge).get(0);

        return graph.builder()
                .addVariableEdge("x", node)
                .build();
    }

    public HeapConfiguration getMaterializationTest() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getMaterializationRes1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getMaterializationRes2() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");

        SelectorLabel nextSel = scene().getSelectorLabel("next");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getTrivialCyclicSLL() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 1, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(0))
                .build();
    }

    /*
     * results in res1 and needs only a single abstraction step
     */
    public HeapConfiguration getCanonizationTest1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getCyclicListHandle() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 1, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(0)}))
                .build();
    }

    public HeapConfiguration getCanonizationRes1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getCanonizationRes3() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    /*
     * This should also result in res1, but needs at least two abstraction steps
     */
    public HeapConfiguration getCanonizationTest2() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 3, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(1), nextSel, nodes.get(2))
                .build();
    }

    public HeapConfiguration getCanonizationTest3() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getOneElemWithVar() {

        HeapConfiguration result = getEmptyGraphWithConstants();

        Type listType = scene().getType("List");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getLongListRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();

        Type listType = scene().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        TIntArrayList nodes = new TIntArrayList();

        return result.builder()
                .addNodes(listType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getLongListRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("de.rwth.i2.attestor.abstraction.programs.LongList");
        SelectorLabel nextSel = scene().getSelectorLabel("next");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getLongListRule3() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("de.rwth.i2.attestor.abstraction.programs.LongList");

        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration getRListRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("RListNode");
        SelectorLabel nextSel = scene().getSelectorLabel("n");

        return result.builder()
                .addNodes(listType, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getRListRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("RListNode");
        SelectorLabel nextSel = scene().getSelectorLabel("n");
        Nonterminal listLabel = scene().createNonterminal("RList", 2, new boolean[]{false, false});

        return result.builder()
                .addNodes(listType, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .addSelector(nodes.get(1), nextSel, nodes.get(2))
                .build();
    }

    public HeapConfiguration testRule1() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Nonterminal n2 = scene().createNonterminal("ReductionTest_N2", 3, new boolean[]{false, false, false});

        Type type = scene().getType("node");
        SelectorLabel sel1 = scene().getSelectorLabel("sel1");

        return result.builder()
                .addNodes(type, 4, nodes)
                .addSelector(nodes.get(0), sel1, nodes.get(3))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(n2, new TIntArrayList(new int[]{nodes.get(3), nodes.get(1), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration testRule2() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Nonterminal n1 = scene().createNonterminal("ReductionTest_N1", 3, new boolean[]{false, false, false});

        Type type = scene().getType("node");

        return result.builder()
                .addNodes(type, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(n1, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(2)}))
                .build();
    }

    public HeapConfiguration testRule3() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("node");
        SelectorLabel sel1 = scene().getSelectorLabel("sel1");
        SelectorLabel sel2 = scene().getSelectorLabel("sel2");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), sel1, nodes.get(1))
                .addSelector(nodes.get(0), sel2, nodes.get(2))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getLongConcreteSLL() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("List");
        SelectorLabel nextSel = scene().getSelectorLabel("next");

        return result.builder()
                .addNodes(listType, 10, nodes)
                .addSelector(nodes.get(0), nextSel, nodes.get(1))
                .addSelector(nodes.get(1), nextSel, nodes.get(2))
                .addSelector(nodes.get(2), nextSel, nodes.get(3))
                .addSelector(nodes.get(3), nextSel, nodes.get(4))
                .addSelector(nodes.get(4), nextSel, nodes.get(5))
                .addSelector(nodes.get(5), nextSel, nodes.get(6))
                .addSelector(nodes.get(6), nextSel, nodes.get(7))
                .addSelector(nodes.get(7), nextSel, nodes.get(8))
                .addSelector(nodes.get(8), nextSel, nodes.get(9))
                .build();
    }

    public HeapConfiguration getSLLHandle() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type listType = scene().getType("List");
        Nonterminal listLabel = scene().createNonterminal("List", 2, new boolean[]{false, true});

        return result.builder()
                .addNodes(listType, 2, nodes)
                .addNonterminalEdge(listLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpectedResultTestGenerateNew() {

        HeapConfiguration result = getEmptyGraphWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("type");

        return result.builder()
                .addNodes(type, 1, nodes)
                .build();
    }

    public HeapConfiguration getExepectedResultTestNewExprTest() {

        HeapConfiguration result = getThreeElementDLLWithConstants();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("NewExprTestNode");
        SelectorLabel next = scene().getSelectorLabel("next");

        return result.builder()
                .addNodes(type, 1, nodes)
                .addSelector(nodes.get(0), next, result.variableTargetOf(Constants.NULL))
                .build();
    }

    public HeapConfiguration getExpectedResult_AssignInvokeNonTrivial() {

        HeapConfiguration result = getListAndConstants();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("AssignInvokeTestNonTrivial");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");

        return result.builder()
                .addNodes(type, 1, nodes)
                .addVariableEdge("x", nodes.get(0))
                .addSelector(nodes.get(0), next, result.variableTargetOf(Constants.NULL))
                .addSelector(nodes.get(0), prev, result.variableTargetOf(Constants.NULL))
                .build();
    }

    public HeapConfiguration getExpectedResult_AssignStmt() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("node");

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        Nonterminal nT = scene().createNonterminal("TLL", 4, new boolean[]{false, false, false, false});

        return result.builder()
                .addNodes(type, 7, nodes)
                .addSelector(nodes.get(0), left, nodes.get(1))
                .addSelector(nodes.get(0), right, nodes.get(2))
                .addSelector(nodes.get(0), parent, nodes.get(3))
                .addVariableEdge("XYZ", nodes.get(2))
                .addVariableEdge("ZYX", nodes.get(0))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(1), nodes.get(0), nodes.get(5), nodes.get(4)}))
                .addNonterminalEdge(nT, new TIntArrayList(new int[]{nodes.get(2), nodes.get(0), nodes.get(5), nodes.get(6)}))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(4))
                .setExternal(nodes.get(6))
                .build();
    }

    public HeapConfiguration getInput_InvokeWithEffect() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), next, nodes.get(2))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(0), prev, nodes.get(2))
                .addVariableEdge(Constants.NULL, nodes.get(2))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getExpectedResult_InvokeWithEffect() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(1), next, nodes.get(1))
                .addSelector(nodes.get(1), prev, nodes.get(0))
                .addSelector(nodes.get(0), prev, nodes.get(2))
                .addVariableEdge(Constants.NULL, nodes.get(2))
                .addVariableEdge("x", nodes.get(0))
                .build();
    }

    public HeapConfiguration getInput_changeSelectorLabel() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel basicLeft = scene().getSelectorLabel("left");
        SelectorLabel basicRight = scene().getSelectorLabel("right");

        AnnotatedSelectorLabel left = new AnnotatedSelectorLabel(basicLeft, "?");
        AnnotatedSelectorLabel right = new AnnotatedSelectorLabel(basicRight, "?");
        Type type = scene().getType("type");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), left, nodes.get(1))
                .addSelector(nodes.get(0), right, nodes.get(1))
                .addSelector(nodes.get(1), left, nodes.get(2))
                .addSelector(nodes.get(1), right, nodes.get(2))
                .addVariableEdge("x", nodes.get(1))
                .build();
    }

    public HeapConfiguration getExpected_changeSelectorLabel() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        SelectorLabel basicLeft = scene().getSelectorLabel("left");
        SelectorLabel basicRight = scene().getSelectorLabel("right");
        AnnotatedSelectorLabel left = new AnnotatedSelectorLabel(basicLeft, "?");
        AnnotatedSelectorLabel left2 = new AnnotatedSelectorLabel(basicLeft, "2");
        AnnotatedSelectorLabel right = new AnnotatedSelectorLabel(basicRight, "?");
        Type type = scene().getType("type");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), left, nodes.get(1))
                .addSelector(nodes.get(0), right, nodes.get(1))
                .addSelector(nodes.get(1), left2, nodes.get(2))
                .addSelector(nodes.get(1), right, nodes.get(2))
                .addVariableEdge("x", nodes.get(1))
                .build();
    }

    public HeapConfiguration getTreeLeaf() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");

        return result.builder()
                .addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(0), prev, nodes.get(1))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .build();
    }

    public HeapConfiguration get2TreeLeaf() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        SelectorLabel parent = scene().getSelectorLabel("parent");

        return result.builder()
                .addNodes(type, 3, nodes)
                .addSelector(nodes.get(0), next, nodes.get(1))
                .addSelector(nodes.get(0), prev, nodes.get(1))
                .addSelector(nodes.get(0), parent, nodes.get(2))
                .addSelector(nodes.get(2), next, nodes.get(1))
                .addSelector(nodes.get(2), prev, nodes.get(1))
                .addSelector(nodes.get(2), parent, nodes.get(1))
                .addVariableEdge("nil", nodes.get(1))
                .build();
    }

    public HeapConfiguration getDLL2Rule() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal dllLabel = scene().createNonterminal("DLL", 3, new boolean[]{true, false, true});

        return result.builder()
                .addNodes(type, 4, nodes)
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getDLLTarget() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        SelectorLabel next = scene().getSelectorLabel("next");
        SelectorLabel prev = scene().getSelectorLabel("prev");
        SelectorLabel list = scene().getSelectorLabel("list");
        Nonterminal dllLabel = scene().createNonterminal("DLL", 3, new boolean[]{true, false, true});

        return result.builder()
                .addNodes(type, 6, nodes)
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .addVariableEdge("$r0", nodes.get(0))
                .addSelector(nodes.get(1), list, nodes.get(0))
                .addSelector(nodes.get(4), next, nodes.get(1))
                .addSelector(nodes.get(4), list, nodes.get(0))
                .addSelector(nodes.get(1), prev, nodes.get(4))
                .addVariableEdge("$r1", nodes.get(4))
                //.addSelector(nodes.get(4), list, nodes.get(5))
                .build();
    }

    public HeapConfiguration getInput_testHash() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal dllLabel = scene().createNonterminal("DLL", 3, new boolean[]{true, false, true});

        return result.builder()
                .addNodes(type, 4, nodes)
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(0), nodes.get(3), nodes.get(2)}))
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getInput_testHash_Permuted() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal dllLabel = scene().createNonterminal("DLL", 3, new boolean[]{true, false, true});

        return result.builder()
                .addNodes(type, 4, nodes)
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(1), nodes.get(0)}))
                .addNonterminalEdge(dllLabel, new TIntArrayList(new int[]{nodes.get(3), nodes.get(0), nodes.get(2)}))
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(2))
                .build();
    }

    public HeapConfiguration getInput_DifferentIndices_1() {

        IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
        List<IndexSymbol> index = new ArrayList<>();
        index.add(abstractIndexSymbol);

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal bnt = scene().createNonterminal("DifferentIndices", 1, new boolean[]{false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        return result.builder()
                .addNodes(type, 1, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
                .build();
    }

    public HeapConfiguration getInput_DifferentIndices_2() {

        IndexSymbol concreteIndexSymbol = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol abstractIndexSymbol = AbstractIndexSymbol.get("X");
        List<IndexSymbol> index = new ArrayList<>();
        index.add(concreteIndexSymbol);
        index.add(abstractIndexSymbol);

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal bnt = scene().createNonterminal("DifferentIndices", 1, new boolean[]{false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        return result.builder()
                .addNodes(type, 1, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0)}))
                .build();
    }

    public HeapConfiguration getInput_EnoughAbstractionDistance() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal tree = scene().createNonterminal("tree", 2, new boolean[]{false, false});
        Nonterminal path = scene().createNonterminal("path", 3, new boolean[]{false, false});

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");

        return result.builder()
                .addNodes(type, 9, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(0))
                .addVariableEdge("x", nodes.get(3))
                .addSelector(nodes.get(3), left, nodes.get(1))
                .addSelector(nodes.get(3), right, nodes.get(2))
                .addSelector(nodes.get(4), left, nodes.get(3))
                .addSelector(nodes.get(4), right, nodes.get(0))
                .addNonterminalEdge(tree)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(tree)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(4))
                .addTentacle(nodes.get(5))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(5))
                .addTentacle(nodes.get(6))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(6))
                .addTentacle(nodes.get(7))
                .addTentacle(nodes.get(0))
                .build()
                .build();

    }

    public HeapConfiguration getPattern_PathAbstraction() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal path = scene().createNonterminal("path", 3, new boolean[]{false, false});


        return result.builder()
                .addNodes(type, 4, nodes)
                .setExternal(nodes.get(3))
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(0))
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(0))
                .build()
                .build();

    }

    public HeapConfiguration getInput_NotEnoughAbstractionDistance() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        SelectorLabel left = scene().getSelectorLabel("left");

        return result.builder()
                .addNodes(type, 2, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(0))
                .addVariableEdge("y", nodes.get(1))
                .addSelector(nodes.get(1), left, nodes.get(0))
                .build();
    }

    public HeapConfiguration getPattern_GraphAbstraction() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");

        SelectorLabel left = scene().getSelectorLabel("left");

        return result.builder()
                .addNodes(type, 2, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(0))
                .addSelector(nodes.get(1), left, nodes.get(0))
                .build();
    }

    public HeapConfiguration getInput_OnlyNonterminalEdgesToAbstract() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal path = scene().createNonterminal("path", 3, new boolean[]{false, false});

        SelectorLabel left = scene().getSelectorLabel("left");
        return result.builder()
                .addNodes(type, 4, nodes)
                .addVariableEdge(Constants.NULL, nodes.get(0))
                .addVariableEdge("x", nodes.get(1))
                .addSelector(nodes.get(1), left, nodes.get(0))
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(0))
                .build()
                .addNonterminalEdge(path)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(3))
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }

    public HeapConfiguration getInput_variableContains0() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal tree = scene().createNonterminal("tree", 2, new boolean[]{false, false});

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");

        return result.builder().addNodes(type, 3, nodes)
                .addVariableEdge("some-variable0", nodes.get(1))
                .addVariableEdge(Constants.NULL, nodes.get(0))
                .addSelector(nodes.get(1), right, nodes.get(2))
                .addSelector(nodes.get(1), left, nodes.get(0))
                .addNonterminalEdge(tree)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }

    public HeapConfiguration getPattern_variableContains0() {

        HeapConfiguration result = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();

        Type type = scene().getType("List");
        Nonterminal tree = scene().createNonterminal("tree", 2, new boolean[]{false, false});

        SelectorLabel left = scene().getSelectorLabel("left");
        SelectorLabel right = scene().getSelectorLabel("right");

        return result.builder().addNodes(type, 3, nodes)
                .setExternal(nodes.get(1))
                .setExternal(nodes.get(0))
                .addSelector(nodes.get(1), right, nodes.get(2))
                .addSelector(nodes.get(1), left, nodes.get(0))
                .addNonterminalEdge(tree)
                .addTentacle(nodes.get(2))
                .addTentacle(nodes.get(0))
                .build()
                .build();
    }
}
