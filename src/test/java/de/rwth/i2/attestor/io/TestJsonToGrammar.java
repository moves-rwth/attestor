package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.io.jsonImport.JsonToGrammar;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import de.rwth.i2.attestor.programState.indexedState.index.ConcreteIndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexSymbol;
import de.rwth.i2.attestor.programState.indexedState.index.IndexVariable;
import org.json.JSONArray;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class TestJsonToGrammar {

    @Test
    public void testParseForwardGrammar() {

        String grammarEncoding = "[	\n"
                + "  {\n"
                + "	\"nonterminal\":\"TestJson\",\n"
                + "	\"rank\":2,\n"
                + "	\"redundandTentacles\":[false,false],\n"
                + "	\"index\":[\"s\",\"()\"],\n"
                + "	\"rules\":[\n"
                + "		{\n"
                + "			\"nodes\":[{\n"
                + "				\"type\":\"type\",\n"
                + "				\"number\":2\n"
                + "			}],\n"
                + "			\"externals\":[0,1],\n"
                + "			\"variables\":[],\n"
                + "			\"selectors\":[],\n"
                + "			\"hyperedges\":[\n"
                + "				{\n"
                + "					\"label\":\"TestJson\",\n"
                + "					\"tentacles\":[0,1],\n"
                + "					\"index\":[\"()\"]\n"
                + "				}\n"
                + "			]\n"
                + "		}]\n"
                + "  },\n"
                + "  {\n"
                + "	\"nonterminal\":\"TestJson\",\n"
                + "	\"rank\":2,\n"
                + "	\"redundandTentacles\":[false,false],\n"
                + "	\"index\":[\"Z\"],\n"
                + "	\"rules\":[\n"
                + "		{\n"
                + "			\"nodes\":[{\n"
                + "				\"type\":\"type\",\n"
                + "				\"number\":2\n"
                + "			}],\n"
                + "			\"externals\":[0,1],\n"
                + "			\"variables\":[],\n"
                + "			\"selectors\":[\n"
                + "				{\n"
                + "					\"label\":\"label\",\n"
                + "					\"annotation\":\"ann\",\n"
                + "					\"origin\":0,\n"
                + "					\"target\":1\n"
                + "				}\n"
                + "			],\n"
                + "			\"hyperedges\":[]\n"
                + "		}\n"
                + "	]\n"
                + "  }\n"
                + "]\n";


        JSONArray jsonArray = new JSONArray(grammarEncoding);

        SceneObject sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);
        ExpectedHCs expectedHCs = new ExpectedHCs(sceneObject);

        Grammar grammar = Grammar.builder()
                .addRules(new JsonToGrammar(sceneObject, new MockupHeapConfigurationRenaming())
                        .parseForwardGrammar(jsonArray))
                .build();

        assertEquals(2, grammar.getAllLeftHandSides().size());
        IndexSymbol s = ConcreteIndexSymbol.getIndexSymbol("s", false);
        IndexSymbol bottom = ConcreteIndexSymbol.getIndexSymbol("Z", true);
        IndexSymbol var = IndexVariable.getIndexVariable();
        List<IndexSymbol> index1 = new ArrayList<>();
        index1.add(s);
        index1.add(var);
        Nonterminal bnt = sceneObject.scene().createNonterminal("TestJson", 2, new boolean[]{false, true});
        IndexedNonterminal nt1 = new IndexedNonterminalImpl(bnt, index1);
        List<IndexSymbol> index2 = new ArrayList<>();
        index2.add(bottom);
        IndexedNonterminal nt2 = new IndexedNonterminalImpl(bnt, index2);

        assertTrue(grammar.getAllLeftHandSides().contains(nt1));
        assertTrue(grammar.getAllLeftHandSides().contains(nt2));
        assertNotNull(grammar.getRightHandSidesFor(nt1));
        assertEquals(1, grammar.getRightHandSidesFor(nt1).size());
        assertTrue("rule graph of nt1", grammar.getRightHandSidesFor(nt1).contains(expectedHCs.getExpected_Rule1()));
        assertNotNull(grammar.getRightHandSidesFor(nt2));
        assertEquals(1, grammar.getRightHandSidesFor(nt2).size());
        assertTrue("rule graph of nt2", grammar.getRightHandSidesFor(nt2).contains(expectedHCs.getExpected_Rule2()));
    }

}
