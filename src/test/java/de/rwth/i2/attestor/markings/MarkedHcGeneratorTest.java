package de.rwth.i2.attestor.markings;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.environment.SceneObject;
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

   private SceneObject sceneObject;
   private ExampleHcImplFactory hcFactory;

   private Grammar grammar;
   private Nonterminal nt;
   private Type type;

   @Before
   public void setup() {

      UnitTestGlobalSettings.reset();

      sceneObject = new MockupSceneObject();
      hcFactory = new ExampleHcImplFactory(sceneObject);

      nt = BasicNonterminal.getNonterminal( "List", 2, new boolean []{false,true} );
      type = sceneObject.scene().getType("List");
      Map<Nonterminal, Set<HeapConfiguration>> rules = new HashMap<>();
      rules.put(nt, new HashSet<>());
      rules.get(nt).add(hcFactory.getListRule1());
      rules.get(nt).add(hcFactory.getListRule2());
      rules.get(nt).add(hcFactory.getListRule3());

      grammar = new Grammar(rules);
   }

   @Test
   public void testSingleMarkingNoNonterminals() {

      HeapConfiguration hc = hcFactory.getEmptyGraphWithConstants();
      Marking marking = new Marking("x");

      Set<HeapConfiguration> expectedMarkedHcs = new HashSet<>();

      MarkedHcGenerator generator = new MarkedHcGenerator(sceneObject, hc, grammar, marking);
      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }

   @Test
   public void testMultipleMarkingsNoNonterminals() {

      HeapConfiguration hc = hcFactory.getTree();
      SelectorLabel left = sceneObject.scene().getSelectorLabel("left");
      SelectorLabel right = sceneObject.scene().getSelectorLabel("right");

      Marking marking = new Marking("y", left, right);

      Set<HeapConfiguration> expectedMarkedHcs = new HashSet<>();
      expectedMarkedHcs.add( hc.clone().builder()
              .addVariableEdge(marking.getUniversalVariableName(), 0)
              .addVariableEdge(marking.getSelectorVariableName("left"), 1)
              .addVariableEdge(marking.getSelectorVariableName("right"), 2)
              .build()
      );

      MarkedHcGenerator generator = new MarkedHcGenerator(sceneObject, hc, grammar, marking);
      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }

   @Test
   public void testSingleMarkingWithNonterminals() {

      HeapConfiguration hc = hcFactory.getEmptyHc();
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
      HeapConfiguration unfoldedHc = hcFactory.getEmptyHc()
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

      MarkedHcGenerator generator = new MarkedHcGenerator(sceneObject, hc, grammar, marking);

      assertEquals(expectedMarkedHcs, generator.getMarkedHcs());
   }
}
