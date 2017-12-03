package de.rwth.i2.attestor.io.jsonExport.inputFormat;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.ipa.IpaContractCollection;
import de.rwth.i2.attestor.util.Pair;
import org.json.JSONWriter;

import java.io.Writer;
import java.util.List;

public class ContractToInputFormatExporter {

    protected Writer writer;

    public ContractToInputFormatExporter(Writer writer) {

        this.writer = writer;
    }


    public void export(String signature, IpaContractCollection contracts) {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                .key("method")
                .value(signature)
                .key("contracts")
                .array(); // the list of contracts <Precondition,List<Postcondition>>

        for (Pair<HeapConfiguration, List<HeapConfiguration>> contract : contracts.getContractList()) {
            jsonWriter.object()
                    .key("precondition")
                    .value(HCtoInputFormatExporter.getInInputFormat(contract.first()))
                    .key("postconditions")
                    .array();
            for (HeapConfiguration postcondition : contract.second()) {
                jsonWriter.value(HCtoInputFormatExporter.getInInputFormat(postcondition));
            }
            jsonWriter.endArray()
                    .endObject();
        }

        jsonWriter.endArray()
                .endObject();

    }

}
