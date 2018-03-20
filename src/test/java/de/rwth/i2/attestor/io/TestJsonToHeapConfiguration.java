package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.jsonImport.JsonToHeapConfiguration;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestJsonToHeapConfiguration {

    private Consumer<String> sink = s -> {
    };
    private SceneObject sceneObject;
    private ExpectedHCs expectedHCs;

    @Before
    public void initClass() {

        sceneObject = new MockupSceneObject();
        sceneObject.scene().options().setIndexedModeEnabled(true);
        expectedHCs = new ExpectedHCs(sceneObject);
        sceneObject.scene().createNonterminal("TestJson", 2, new boolean[]{false, false});
    }

    @Test
    public void testAnnotated() {

        String graphEncoding = "{\n"
                + "	\"nodes\":[\n"
                + "		{\n"
                + "			\"type\":\"type\",\n"
                + "			\"number\":2\n"
                + "		}\n"
                + "	],\n"
                + "	\"externals\":[],\n"
                + "	\"variables\":[],\n"
                + "	\"selectors\":[\n"
                + "		{\n"
                + "			\"label\":\"label\",\n"
                + "			\"annotation\":\"ann\",\n"
                + "			\"origin\":0,\n"
                + "			\"target\":1\n"
                + "		}\n"
                + "	],\n"
                + "	\"hyperedges\":[]\n"
                + "}";

        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                new MockupHeapConfigurationRenaming());
        HeapConfiguration parsed
                = importer.parse(new JSONObject(graphEncoding), sink);

        assertEquals(expectedHCs.getExpected_Annotated(), parsed);
    }

    @Test
    public void testBottom() {

        String graphEncoding = "{\n"
                + "	\"nodes\":[\n"
                + "		{\n"
                + "			\"type\":\"type\",\n"
                + "			\"number\":2\n"
                + "		}\n"
                + "	],\n"
                + "	\"externals\":[],\n"
                + "	\"variables\":[],\n"
                + "	\"selectors\":[],\n"
                + "	\"hyperedges\":[\n"
                + "			{\n"
                + "			\"label\":\"TestJson\",\n"
                + "			\"index\":[\"Z\"],\n"
                + "			\"tentacles\":[0,1]\n"
                + "		}]\n"
                + "}\n";


        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                new MockupHeapConfigurationRenaming());
        HeapConfiguration parsed = importer.parse(new JSONObject(graphEncoding), sink);

        assertEquals(expectedHCs.getExpected_Bottom(), parsed);
    }

    @Test
    public void testTwoElementIndex() {

        String graphEncoding = "{\n"
                + "	\"nodes\":[\n"
                + "		{\n"
                + "			\"type\":\"type\",\n"
                + "			\"number\":2\n"
                + "		}\n"
                + "	],\n"
                + "	\"externals\":[],\n"
                + "	\"variables\":[],\n"
                + "	\"selectors\":[],\n"
                + "	\"hyperedges\":[\n"
                + "			{\n"
                + "			\"label\":\"TestJson\",\n"
                + "			\"index\":[\"s\",\"Z\"],\n"
                + "			\"tentacles\":[0,1]\n"
                + "		}]\n"
                + "}\n";

        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                new MockupHeapConfigurationRenaming());
        HeapConfiguration parsed = importer.parse(new JSONObject(graphEncoding), sink);

        assertEquals(expectedHCs.getExpected_TwoElementIndex(), parsed);
    }

    @Test
    public void testIndexWithVar() {

        String graphEncoding = "{\n"
                + "	\"nodes\":[\n"
                + "		{\n"
                + "			\"type\":\"type\",\n"
                + "			\"number\":2\n"
                + "		}\n"
                + "	],\n"
                + "	\"externals\":[],\n"
                + "	\"variables\":[],\n"
                + "	\"selectors\":[],\n"
                + "	\"hyperedges\":[\n"
                + "			{\n"
                + "			\"label\":\"TestJson\",\n"
                + "			\"index\":[\"s\",\"()\"],\n"
                + "			\"tentacles\":[0,1]\n"
                + "		}]\n"
                + "}\n";

        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                new MockupHeapConfigurationRenaming());
        HeapConfiguration parsed = importer.parse(new JSONObject(graphEncoding), sink);

        assertEquals(expectedHCs.getExpected_IndexWithVar(), parsed);
    }

    @Test
    public void testAbstractIndex() {

        String graphEncoding = "{\n"
                + "	\"nodes\":[\n"
                + "		{\n"
                + "			\"type\":\"type\",\n"
                + "			\"number\":2\n"
                + "		}\n"
                + "	],\n"
                + "	\"externals\":[],\n"
                + "	\"variables\":[],\n"
                + "	\"selectors\":[],\n"
                + "	\"hyperedges\":[\n"
                + "			{\n"
                + "			\"label\":\"TestJson\",\n"
                + "			\"index\":[\"s\",\"_X\"],\n"
                + "			\"tentacles\":[0,1]\n"
                + "		}]\n"
                + "}\n";

        JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                new MockupHeapConfigurationRenaming());
        HeapConfiguration parsed = importer.parse(new JSONObject(graphEncoding), sink);

        assertEquals(expectedHCs.getExpected_IndexWithAbs(), parsed);
    }

    @Test
    public void testFail_AbstractIndex() {

        try {
            String graphEncoding = "{\n"
                    + "	\"nodes\":[\n"
                    + "		{\n"
                    + "			\"type\":\"type\",\n"
                    + "			\"number\":2\n"
                    + "		}\n"
                    + "	],\n"
                    + "	\"externals\":[],\n"
                    + "	\"variables\":[],\n"
                    + "	\"selectors\":[],\n"
                    + "	\"hyperedges\":[\n"
                    + "			{\n"
                    + "			\"label\":\"TestJson\",\n"
                    + "			\"index\":[\"_X\",\"s\"],\n"
                    + "			\"tentacles\":[0,1]\n"
                    + "		}]\n"
                    + "}\n";

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                    new MockupHeapConfigurationRenaming());
            importer.parse(new JSONObject(graphEncoding), sink);
            fail("abstract index symbols may only occur at the end of index");
        } catch (AssertionError e) {
            //expected
        }
    }

    @Test
    public void testFail_Bottom() {

        try {
            String graphEncoding = "{\n"
                    + "	\"nodes\":[\n"
                    + "		{\n"
                    + "			\"type\":\"type\",\n"
                    + "			\"number\":2\n"
                    + "		}\n"
                    + "	],\n"
                    + "	\"externals\":[],\n"
                    + "	\"variables\":[],\n"
                    + "	\"selectors\":[],\n"
                    + "	\"hyperedges\":[\n"
                    + "			{\n"
                    + "			\"label\":\"TestJson\",\n"
                    + "			\"index\":[\"Z\",\"s\"],\n"
                    + "			\"tentacles\":[0,1]\n"
                    + "		}]\n"
                    + "}\n";

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                    new MockupHeapConfigurationRenaming());
            importer.parse(new JSONObject(graphEncoding), sink);
            fail("bottom index symbols may only occur at the end of index");
        } catch (AssertionError e) {
            //expected
        }
    }

    @Test
    public void testFail_IndexVariable() {

        try {
            String graphEncoding = "{\n"
                    + "	\"nodes\":[\n"
                    + "		{\n"
                    + "			\"type\":\"type\",\n"
                    + "			\"number\":2\n"
                    + "		}\n"
                    + "	],\n"
                    + "	\"externals\":[],\n"
                    + "	\"variables\":[],\n"
                    + "	\"selectors\":[],\n"
                    + "	\"hyperedges\":[\n"
                    + "			{\n"
                    + "			\"label\":\"TestJson\",\n"
                    + "			\"index\":[\"()\",\"Z\"],\n"
                    + "			\"tentacles\":[0,1]\n"
                    + "		}]\n"
                    + "}\n";

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                    new MockupHeapConfigurationRenaming());
            importer.parse(new JSONObject(graphEncoding), sink);
            fail("variable index symbols may only occur at the end of index");
        } catch (AssertionError e) {
            //expected
        }
    }

    @Test
    public void testFail_noBottom() {

        try {
            String graphEncoding = "{\n"
                    + "	\"nodes\":[\n"
                    + "		{\n"
                    + "			\"type\":\"type\",\n"
                    + "			\"number\":2\n"
                    + "		}\n"
                    + "	],\n"
                    + "	\"externals\":[],\n"
                    + "	\"variables\":[],\n"
                    + "	\"selectors\":[],\n"
                    + "	\"hyperedges\":[\n"
                    + "			{\n"
                    + "			\"label\":\"TestJson\",\n"
                    + "			\"index\":[\"s\",\"s\"],\n"
                    + "			\"tentacles\":[0,1]\n"
                    + "		}]\n"
                    + "}\n";

            JsonToHeapConfiguration importer = new JsonToHeapConfiguration(sceneObject,
                    new MockupHeapConfigurationRenaming());
            importer.parse(new JSONObject(graphEncoding), sink);
            fail("abstract index symbols may only occur at the end of index");
        } catch (AssertionError e) {
            //expected
        }
    }


}
