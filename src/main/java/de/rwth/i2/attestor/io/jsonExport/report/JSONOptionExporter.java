package de.rwth.i2.attestor.io.jsonExport.report;

import de.rwth.i2.attestor.main.scene.Options;
import de.rwth.i2.attestor.main.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONWriter;

import java.io.Writer;

/**
 * Created by christina on 19.12.17.
 */
public class JSONOptionExporter {

    protected final Writer writer;

    private static final Logger logger = LogManager.getLogger("JSONOptionExporter");


    public JSONOptionExporter(Writer writer) {

        this.writer = writer;
    }

    public void exportForReport(Scene scene){

        JSONWriter jsonWriter = new JSONWriter(writer);

        jsonWriter.array();

        // Specify which options should be exported
        String[] optionNames = {"mode", "abstractionDistance", "maximalStateSpace", "maximalHeap", "removeDeadVariables", "aggressiveNullAbstraction", "garbageCollection", "stateSpacePostProcessing"};
        Options options = scene.options();

        /* Retrieve all options together with its values */
        for(String optionName : optionNames){
            String value = "";

            switch(optionName) {
                case "mode":
                    if (options.isIndexedMode()) {
                        value = "indexed";
                    } else {
                        value = "normal";
                    }
                    break;
                case "abstractionDistance":
                    value = String.valueOf(options.getAbstractionDistance());
                    break;
                case "maximalStateSpace":
                    value = String.valueOf(options.getMaxStateSpaceSize());
                    break;
                case "maximalHeap":
                    value = String.valueOf(options.getMaxStateSize());
                    break;
                case "removeDeadVariables":
                    value = String.valueOf(options.isRemoveDeadVariables());
                    break;
                case "aggressiveNullAbstraction":
                    value = String.valueOf(options.getAggressiveNullAbstraction());
                    break;
                case "garbageCollection":
                    value = String.valueOf(options.isGarbageCollectionEnabled());
                    break;
                case "stateSpacePostProcessing":
                    value = String.valueOf(options.isPostprocessingEnabled());
                    break;
                default:
                    logger.error("Cannot export option value for option: " + optionName + ". Option handling not specified.");
                    break;
            }
            writeOption(jsonWriter, optionName, value);
        }

        jsonWriter.endArray();

    }

    private void writeOption(JSONWriter jsonWriter, String name, String value){
        jsonWriter.object()
                .key("name")
                .value(name)
                .key("value")
                .value(value)
                .endObject();
    }
}
