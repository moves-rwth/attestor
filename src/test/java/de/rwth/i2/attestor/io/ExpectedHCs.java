package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.AbstractIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

class ExpectedHCs extends SceneObject {

    protected ExpectedHCs(SceneObject otherObject) {

        super(otherObject);
    }

    public HeapConfiguration getExpected_Annotated() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        SelectorLabel basicSel = scene().getSelectorLabel("label");
        AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel(basicSel, "ann");

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getExpected_Bottom() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        List<IndexSymbol> index = new ArrayList<>();
        index.add(bottom);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpected_TwoElementIndex() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(bottom);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpected_IndexWithVar() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol var = IndexVariable.getIndexVariable();
        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(var);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpected_IndexWithAbs() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol abs = AbstractIndexSymbol.get("X");
        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(abs);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpected_AbstractIndex() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol abs = AbstractIndexSymbol.get("X");
        List<IndexSymbol> index = new ArrayList<>();
        index.add(s);
        index.add(abs);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }

    public HeapConfiguration getExpected_Rule2() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        SelectorLabel basicSel = scene().getSelectorLabel("label");
        AnnotatedSelectorLabel sel = new AnnotatedSelectorLabel(basicSel, "ann");

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();
    }

    public HeapConfiguration getExpected_Rule1() {

        HeapConfiguration hc = new InternalHeapConfiguration();

        IndexSymbol var = IndexVariable.getIndexVariable();
        List<IndexSymbol> index = new ArrayList<>();
        index.add(var);
        Nonterminal bnt = scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
        IndexedNonterminal nt = new IndexedNonterminalImpl(bnt, index);

        Type type = scene().getType("type");

        TIntArrayList nodes = new TIntArrayList();
        return hc.builder().addNodes(type, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addNonterminalEdge(nt, new TIntArrayList(new int[]{nodes.get(0), nodes.get(1)}))
                .build();
    }


}
