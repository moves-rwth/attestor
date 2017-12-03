package de.rwth.i2.attestor.io.hcToJava;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.scene.SceneObject;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class HcToJavaTest {

    @Test
    public void testSimple() {

        SceneObject sceneObject = new MockupSceneObject();
        ExampleHcImplFactory hcFactory = new ExampleHcImplFactory(sceneObject);

        HeapConfiguration hc = hcFactory.getLongConcreteSLL()
                .builder().addVariableEdge("foo", 0).build();

        OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());

        HcToJava export = new HcToJava(hc, writer, "\t");
        try {
            export.translateHc();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
