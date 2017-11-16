package de.rwth.i2.attestor.io.jsonExport.cytoscapeFormat;

import java.io.*;
import java.util.List;

import org.json.JSONWriter;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.io.CustomHcListExporter;
import de.rwth.i2.attestor.io.FileUtils;
import de.rwth.i2.attestor.io.jsonImport.HcLabelPair;

/**
 * Created by christina on 23.08.17.
 */
public class JsonCustomHcListExporter implements CustomHcListExporter {


    public void export(String directory, List<HcLabelPair> hcList) throws IOException {
        // Export summary json
        FileUtils.createDirectories(directory);
        FileWriter writer = new FileWriter(directory + File.separator + "hcListExport.json");
        exportSummary(writer, hcList);
        writer.close();

        // Export the single hcs
        for(HcLabelPair cur : hcList){
            exportHeapConfiguration(directory + File.separator + cur.getLabel() + ".json",
                        cur.getHc());
        }

    }

    private void exportSummary(Writer writer, List<HcLabelPair> list) {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.array();
        for(HcLabelPair cur : list) {

            String label = cur.getLabel();

            jsonWriter.object()
                    .key("name").value(label);
            jsonWriter.endObject();
        }
        jsonWriter.endArray();

    }

    private void exportHeapConfiguration(String filename, HeapConfiguration hc)
            throws IOException {

        FileWriter writer = new FileWriter(filename);
        HeapConfigurationExporter exporter = new JsonExtendedHeapConfigurationExporter(writer);
        exporter.export(hc);
        writer.close();
    }

}
