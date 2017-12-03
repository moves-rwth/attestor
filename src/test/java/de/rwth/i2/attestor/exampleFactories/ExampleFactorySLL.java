package de.rwth.i2.attestor.exampleFactories;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.util.Constants;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.Collections;
import java.util.List;

public class ExampleFactorySLL extends AbstractExampleFactory {


    private Nonterminal nt;
    private SelectorLabel sel;

    public ExampleFactorySLL(SceneObject sceneObject) {

        super(sceneObject);
        nt = scene().createNonterminal("SLL", 2, new boolean[]{false, true});
        sel = scene().getSelectorLabel("next");
    }

    @Override
    public List<Nonterminal> getNonterminals() {

        return Collections.singletonList(nt);
    }

    @Override
    public List<SelectorLabel> getSelectorLabels() {

        return Collections.singletonList(sel);
    }

    @Override
    public Type getNodeType() {

        Type result = scene().getType("list");
        SelectorLabel next = scene().getSelectorLabel("next");
        result.addSelectorLabel(next, Constants.NULL);
        return result;
    }

    @Override
    public Grammar getGrammar() {

        return Grammar
                .builder()
                .addRule(nt, getSLLRule1())
                .addRule(nt, getSLLRule2())
                .addRule(nt, getSLLRule3())
                .build();
    }

    @Override
    public HeapConfiguration getInput() {

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration()
                .builder()
                .addNodes(getNodeType(), 2, nodes)
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .build();
    }

    public SelectorLabel getNextSel() {

        return sel;
    }

    public HeapConfiguration getListofLengthAtLeastOne() {

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration()
                .builder()
                .addNodes(getNodeType(), 3, nodes)
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .build();
    }

    private HeapConfiguration getSLLRule1() {

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration()
                .builder()
                .addNodes(getNodeType(), 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .build();
    }

    private HeapConfiguration getSLLRule2() {

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration()
                .builder()
                .addNodes(getNodeType(), 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), sel, nodes.get(1))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .build();
    }

    private HeapConfiguration getSLLRule3() {

        TIntArrayList nodes = new TIntArrayList();
        return new InternalHeapConfiguration()
                .builder()
                .addNodes(getNodeType(), 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(0))
                .addTentacle(nodes.get(1))
                .build()
                .addNonterminalEdge(nt)
                .addTentacle(nodes.get(1))
                .addTentacle(nodes.get(2))
                .build()
                .build();
    }
}
