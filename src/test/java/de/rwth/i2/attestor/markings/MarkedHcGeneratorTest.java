package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertEquals;

public class MarkedHcGeneratorTest {

   private Grammar grammar;
   private Nonterminal nt;
   private Type type;

   @Before
   public void setup() {

      nt = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
      type = Settings.getInstance().factory().getType("List");
      Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();
      rules.put(nt, new HashSet<>());
      rules.get(nt).add(ExampleHcImplFactory.getListRule1());
      rules.get(nt).add(ExampleHcImplFactory.getListRule2());
      rules.get(nt).add(ExampleHcImplFactory.getListRule3());

      grammar = new Grammar(rules);
   }

   @Test
   public void testSingleMarkingNoNonterminals() {

      HeapConfiguration hc = ExampleHcImplFactory.getEmptyGraphWithConstants();
      Marking marking = new Marking("x");

      Set<HeapConfiguration> expectedMarkedHcs = new HashSet<>();
      expectedMarkedHcs.add( hc.clone().builder().addVariableEdge(marking.getUniversalVariableName(), 0).build() );
      expectedMarkedHcs.add( hc.clone().builder().addVariableEdge(marking.getUniversalVariableName(), 1).build() );
      expectedMarkedHcs.add( hc.clone().builder().addVariableEdge(marking.getUniversalVariableName(), 2).build() );

      MarkedHcGenerator generator = new MarkedHcGenerator(hc, grammar, marking);
      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }

   @Test
   public void testMultipleMarkingsNoNonterminals() {

      HeapConfiguration hc = ExampleHcImplFactory.getTree();
      Marking marking = new Marking("y", "left", "right");

      Set<HeapConfiguration> expectedMarkedHcs = new HashSet<>();
      expectedMarkedHcs.add( hc.clone().builder()
              .addVariableEdge(marking.getUniversalVariableName(), 0)
              .addVariableEdge(marking.getSelectorVariableName("left"), 1)
              .addVariableEdge(marking.getSelectorVariableName("right"), 2)
              .build()
      );

      MarkedHcGenerator generator = new MarkedHcGenerator(hc, grammar, marking);
      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }

   @Test
   public void testSingleMarkingWithNonterminals() {

      HeapConfiguration hc = ExampleHcImplFactory.getEmptyHc();
      TIntArrayList nodes = new TIntArrayList();

      hc = hc.builder()
              .addNodes(type, 2, nodes)
              .addNonterminalEdge(nt)
              .addTentacle(nodes.get(0))
              .addTentacle(nodes.get(1))
              .build()
              .build();

      Marking marking = new Marking("z");
      Set<HeapConfiguration> expectedMarkedHcs = new HashSet<>();

      expectedMarkedHcs.add( hc.clone().builder()
              .addVariableEdge(marking.getUniversalVariableName(), nodes.get(0))
              .build()
      );
      expectedMarkedHcs.add( hc.clone().builder()
              .addVariableEdge(marking.getUniversalVariableName(), nodes.get(1))
              .build()
      );

      nodes.clear();
      HeapConfiguration unfoldedHc = ExampleHcImplFactory.getEmptyHc()
              .builder()
              .addNodes(type, 3, nodes)
              .addNonterminalEdge(nt)
              .addTentacle(nodes.get(0))
              .addTentacle(nodes.get(1))
              .build()
              .addVariableEdge(marking.getUniversalVariableName(), nodes.get(1))
              .addNonterminalEdge(nt)
              .addTentacle(nodes.get(1))
              .addTentacle(nodes.get(2))
              .build()
              .build();
      expectedMarkedHcs.add(unfoldedHc);

      MarkedHcGenerator generator = new MarkedHcGenerator(hc, grammar, marking);

      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }
}
