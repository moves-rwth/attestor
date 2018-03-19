package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.jsonImport.JsonToHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParseTest {

    private static final Logger logger = LogManager.getLogger("ParseTest.java");

    @Test
    public void test() {

        SceneObject sceneObject = new MockupSceneObject();

        try {
            Scanner scan = new Scanner(new FileReader("src/test/resources/GraphEncodingTest.txt"));
            StringBuilder str = new StringBuilder();
            while (scan.hasNext())
                str.append(scan.nextLine());
            scan.close();

            // build a JSON object
            JSONObject obj = new JSONObject(str.toString());

            logger.trace("length: " + obj.length());
            logger.trace("node number: " + obj.getJSONArray("nodes").getJSONObject(0).getInt("number"));

            /*
             * store nonterminal for sake of testing. Normally the nonterminals should be created by reading a grammar
             */
            sceneObject.scene().createNonterminal("Hyperedge", 3, new boolean[]{true, true, true});

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                    new MockupHeapConfigurationRenaming());
            HeapConfiguration res = importer.parse(obj, s -> { });

            logger.trace("res:" + res);

            assertEquals("nr of nodes", 3, res.countNodes());
            assertEquals("nr of externals", 2, res.countExternalNodes());
            assertEquals("nr of hyperedges", 1, res.countNonterminalEdges());
            assertEquals("nr of variables", 2, res.countVariableEdges());
            assertEquals("selector at 0", 1, res.selectorLabelsOf(res.externalNodeAt(0)).size());
            assertEquals("selector at 0 is next",
                    sceneObject.scene().getSelectorLabel("next"),
                    res.selectorLabelsOf(res.externalNodeAt(0)).get(0));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("exception");
        }
    }
}
