package de.rwth.i2.attestor.programState.indexedState;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.grammar.GrammarBuilder;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.InternalHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;

public class BalancedTreeGrammar {

    public AnnotatedSelectorLabel SELECTOR_RIGHT_0;
    public AnnotatedSelectorLabel SELECTOR_RIGHT_1;
    public AnnotatedSelectorLabel SELECTOR_LEFT_M1;
    public AnnotatedSelectorLabel SELECTOR_PARENT;
    public AnnotatedSelectorLabel SELECTOR_RIGHT_M1;
    public AnnotatedSelectorLabel SELECTOR_LEFT_1;
    public AnnotatedSelectorLabel SELECTOR_LEFT_0;
    public boolean[] IS_REDUCTION_TENTACLE = new boolean[]{false, true};
    public int NT_RANK = 2;
    public String NT_LABEL = "B";
    public Type TYPE;
    SceneObject sceneObject;
    private SelectorLabel basicLeft;
    private SelectorLabel basicRight;
    private SelectorLabel basicParent;

    public BalancedTreeGrammar(SceneObject sceneObject) {

        this.sceneObject = sceneObject;
        TYPE = sceneObject.scene().getType("AVLTree");
        basicLeft = sceneObject.scene().getSelectorLabel("left");
        basicRight = sceneObject.scene().getSelectorLabel("right");
        basicParent = sceneObject.scene().getSelectorLabel("parent");
        SELECTOR_RIGHT_0 = new AnnotatedSelectorLabel(basicRight, "0");
        SELECTOR_RIGHT_1 = new AnnotatedSelectorLabel(basicRight, "1");
        SELECTOR_LEFT_M1 = new AnnotatedSelectorLabel(basicLeft, "-1");
        SELECTOR_PARENT = new AnnotatedSelectorLabel(basicParent, "");
        SELECTOR_RIGHT_M1 = new AnnotatedSelectorLabel(basicRight, "-1");
        SELECTOR_LEFT_1 = new AnnotatedSelectorLabel(basicLeft, "1");
        SELECTOR_LEFT_0 = new AnnotatedSelectorLabel(basicLeft, "0");
    }

    public Grammar getGrammar() {

        GrammarBuilder builder = Grammar.builder();
        addRuleBalanced(builder);
        addUnbalancedRuleLeft(builder);
        addUnbalancedRuleRight(builder);
        addLeftLeafRule(builder);
        addRightLeafRule(builder);
        addBalancedLeafRule(builder);
        return builder.build();
    }

    private void addRuleBalanced(GrammarBuilder builder) {

        IndexVariable var = IndexVariable.getIndexVariable();
        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(ConcreteIndexSymbol.getIndexSymbol("s", false));
        lhsIndex.add(var);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createRuleBalanced();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createRuleBalanced() {

        IndexVariable var = IndexVariable.getIndexVariable();

        ArrayList<IndexSymbol> r = new ArrayList<>();
        r.add(var);
        Nonterminal bnt = sceneObject.scene().getNonterminal(NT_LABEL);
        IndexedNonterminal rightNt = new IndexedNonterminalImpl(bnt, r);
        ArrayList<IndexSymbol> l = new ArrayList<>();
        l.add(var);
        IndexedNonterminal leftNt = new IndexedNonterminalImpl(bnt, l);

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .addSelector(nodes.get(0), SELECTOR_LEFT_0, nodes.get(1))
                .addSelector(nodes.get(1), SELECTOR_PARENT, nodes.get(0))
                .addSelector(nodes.get(0), SELECTOR_RIGHT_0, nodes.get(2))
                .addSelector(nodes.get(2), SELECTOR_PARENT, nodes.get(0))
                .addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
                .build();
        return rhs;
    }

    private void addUnbalancedRuleLeft(GrammarBuilder builder) {

        IndexVariable var = IndexVariable.getIndexVariable();
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);

        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(s);
        lhsIndex.add(s);
        lhsIndex.add(var);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createUnbalancedRuleLeft();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createUnbalancedRuleLeft() {

        IndexVariable var = IndexVariable.getIndexVariable();
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);

        ArrayList<IndexSymbol> r = new ArrayList<>();
        r.add(var);
        Nonterminal bnt = sceneObject.scene().getNonterminal(NT_LABEL);
        IndexedNonterminal rightNt = new IndexedNonterminalImpl(bnt, r);
        ArrayList<IndexSymbol> l = new ArrayList<>();
        l.add(s);
        l.add(var);
        IndexedNonterminal leftNt = new IndexedNonterminalImpl(bnt, l);
        AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_1;
        AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_M1;
        AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addSelector(nodes.get(2), parentLabel, nodes.get(0))
                .addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
                .build();
        return rhs;
    }

    private void addUnbalancedRuleRight(GrammarBuilder builder) {

        IndexVariable var = IndexVariable.getIndexVariable();
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);

        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(s);
        lhsIndex.add(s);
        lhsIndex.add(var);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createUnbalancedRuleRight();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createUnbalancedRuleRight() {

        IndexVariable var = IndexVariable.getIndexVariable();
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);

        ArrayList<IndexSymbol> r = new ArrayList<>();
        r.add(s);
        r.add(var);
        Nonterminal bnt = sceneObject.scene().getNonterminal(NT_LABEL);
        IndexedNonterminal rightNt = new IndexedNonterminalImpl(bnt, r);
        ArrayList<IndexSymbol> l = new ArrayList<>();
        l.add(var);
        IndexedNonterminal leftNt = new IndexedNonterminalImpl(bnt, l);
        AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_M1;
        AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_1;
        AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 4, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(3))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addSelector(nodes.get(2), parentLabel, nodes.get(0))
                .addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(3)}))
                .addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(2), nodes.get(3)}))
                .build();
        return rhs;
    }

    private void addBalancedLeafRule(GrammarBuilder builder) {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(bottom);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createBalancedLeafRule();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createBalancedLeafRule() {

        AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_0;
        AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_0;

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 2, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(1))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(0), rightLabel, nodes.get(1))
                .build();
        return rhs;
    }

    private void addLeftLeafRule(GrammarBuilder builder) {

        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        lhsIndex.add(s);
        lhsIndex.add(bottom);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createLeftLeafRule();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createLeftLeafRule() {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        ArrayList<IndexSymbol> l = new ArrayList<>();
        l.add(bottom);
        Nonterminal bnt = sceneObject.scene().getNonterminal(NT_LABEL);
        IndexedNonterminal leftNt = new IndexedNonterminalImpl(bnt, l);
        AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_1;
        AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_M1;
        AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), leftLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), rightLabel, nodes.get(2))
                .addNonterminalEdge(leftNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
        return rhs;
    }

    private void addRightLeafRule(GrammarBuilder builder) {

        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        ArrayList<IndexSymbol> lhsIndex = new ArrayList<>();
        lhsIndex.add(s);
        lhsIndex.add(bottom);
        Nonterminal bnt = sceneObject.scene().createNonterminal(NT_LABEL, NT_RANK, IS_REDUCTION_TENTACLE);
        IndexedNonterminal lhs = new IndexedNonterminalImpl(bnt, lhsIndex);

        HeapConfiguration rhs = createRightLeafRule();

        builder.addRule(lhs, rhs);
    }

    public HeapConfiguration createRightLeafRule() {

        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);

        ArrayList<IndexSymbol> r = new ArrayList<>();
        r.add(bottom);
        Nonterminal bnt = sceneObject.scene().getNonterminal(NT_LABEL);
        IndexedNonterminal rightNt = new IndexedNonterminalImpl(bnt, r);
        AnnotatedSelectorLabel leftLabel = SELECTOR_LEFT_M1;
        AnnotatedSelectorLabel rightLabel = SELECTOR_RIGHT_1;
        AnnotatedSelectorLabel parentLabel = SELECTOR_PARENT;

        HeapConfiguration rhs = new InternalHeapConfiguration();
        TIntArrayList nodes = new TIntArrayList();
        rhs = rhs.builder().addNodes(TYPE, 3, nodes)
                .setExternal(nodes.get(0))
                .setExternal(nodes.get(2))
                .addSelector(nodes.get(0), rightLabel, nodes.get(1))
                .addSelector(nodes.get(1), parentLabel, nodes.get(0))
                .addSelector(nodes.get(0), leftLabel, nodes.get(2))
                .addNonterminalEdge(rightNt, new TIntArrayList(new int[]{nodes.get(1), nodes.get(2)}))
                .build();
        return rhs;
    }

}
