package de.rwth.i2.attestor.io.jsonExport.inputFormat;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.procedures.Contract;
import org.json.JSONWriter;

import java.io.Writer;
import java.util.Collection;

public class ContractToInputFormatExporter {

    protected Writer writer;

    public ContractToInputFormatExporter(Writer writer) {

        this.writer = writer;
    }


    public void export(String signature, Collection<Contract> contracts) {

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.object()
                .key("method")
                .value(signature)
                .key("contracts")
                .array(); // the list of contracts <Precondition,List<Postcondition>>

        for (Contract contract : contracts) {
            jsonWriter.object()
                    .key("precondition")
                    .value(HCtoInputFormatExporter.getInInputFormat(contract.getPrecondition()))
                    .key("postconditions")
                    .array();
            for (HeapConfiguration postcondition : contract.getPostconditions()) {
                jsonWriter.value(HCtoInputFormatExporter.getInInputFormat(postcondition));
            }
            jsonWriter.endArray()
                    .endObject();
        }

        jsonWriter.endArray()
                .endObject();

    }

}
