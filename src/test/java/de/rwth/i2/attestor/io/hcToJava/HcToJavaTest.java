package de.rwth.i2.attestor.io.hcToJava;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStreamWriter;

public class HcToJavaTest {

    @Test
    @Ignore
    public void testSimple() {

        HeapConfiguration hc = ExampleHcImplFactory.getLongConcreteSLL()
                .builder().addVariableEdge("foo", 0).build();

        OutputStreamWriter writer = new OutputStreamWriter(System.out);

        HcToJava export = new HcToJava(hc, writer, "\t");
        try {
            export.declareVariables();
            export.translateHc();
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
