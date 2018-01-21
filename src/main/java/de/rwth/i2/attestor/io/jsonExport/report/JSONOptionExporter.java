package de.rwth.i2.attestor.io.jsonExport.report;

import de.rwth.i2.attestor.io.HttpExporter;
import de.rwth.i2.attestor.main.scene.Options;
import de.rwth.i2.attestor.main.scene.Scene;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONStringer;
import org.json.JSONWriter;

import java.io.UnsupportedEncodingException;

/**
 * Created by christina on 19.12.17.
 */
public class JSONOptionExporter {

    protected final HttpExporter httpExporter;
    private static final Logger logger = LogManager.getLogger("JSONOptionExporter");


    public JSONOptionExporter(HttpExporter httpExporter) {

        this.httpExporter = httpExporter;
    }


    public void exportForReport(Scene scene){

        JSONStringer jsonStringer = new JSONStringer();

        jsonStringer.array();

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
            writeOption(jsonStringer, optionName, value);
        }

        jsonStringer.endArray();

        try {
            httpExporter.sendOptionsRequest(scene.getIdentifier(),jsonStringer.toString());
        } catch (UnsupportedEncodingException e) {
            // todo, json stringer returns wrong format, this should not happen!!
        }
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
