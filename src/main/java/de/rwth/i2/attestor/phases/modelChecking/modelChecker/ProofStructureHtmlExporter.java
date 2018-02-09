package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ProofStructureHtmlExporter implements ProofStructureExporter {

    private final static Logger logger = LogManager
            .getLogger("ProofStructureToCytoscapeExporter");

    private final static String filePrefix = "ps_";
    private final static String fileSuffix = ".html";
    private final static String htmlTemplateHead1 = "<head>\n" + "		<title>";
    private final static String htmlTemplateHead2 = "</title>\n"
            + "\n"
            + "		<script src=\"https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.1.2/cytoscape.js\"></script>\n"
            + "\n"
            + "		<!-- for testing with local version of cytoscape.js -->\n"
            + "		<!--<script src=\"../cytoscape.js/build/cytoscape.js\"></script>-->\n"
            + "\n" + "\n" + "	\n" + "		\n" + "		<style>\n" + "			body {\n"
            + "				font-family: helvetica;\n" + "				font-size: 14px;\n"
            + "			}\n" + "\n" + "			#cy {\n" + "				width: 100%;\n"
            + "				height: 100%;\n" + "				position: absolute;\n"
            + "				left: 0;\n" + "				top: 0;\n" + "				z-index: 999;\n"
            + "			}\n" + "\n" + "			h1 {\n" + "				opacity: 0.5;\n"
            + "				font-size: 1em;\n" + "			}\n" + "		</style>\n" + "		\n"
            + "</head>\n\n";
    private final static String htmlTemplateBody1 = "<body>\n"
            + "  <div id=\"cy\"></div>\n"
            + "<script>\n"
            + "    var cy = cytoscape({\n"
            + "      container: document.getElementById('cy'),\n"
            + "      boxSelectionEnabled: false,\n"
            + "      autounselectify: true,\n"
            + "      style: [\n"
            + "      {\n" + "	selector: 'node[type=\"node\"]',\n"
            + "	style: {\n" + "	  'content': 'data(label)',\n"
            + "	  'text-opacity': 0.5,\n" + "	  'text-valign': 'center',\n"
            + "	  'text-halign': 'center',\n"
            + "	  'background-color': '#d3d3d3',\n" + "	  'shape' : 'circle'\n"
            + "	}\n" + "      },\n"
            + "      {\n" + "	selector: 'node[type=\"nodeSucc\"]',\n"
            + "	style: {\n" + "	  'content': 'data(label)',\n"
            + "	  'text-opacity': 0.5,\n" + "	  'text-valign': 'center',\n"
            + "	  'text-halign': 'center',\n"
            + "	  'background-color': '#00ff00',\n" + "	  'shape' : 'circle'\n"
            + "	}\n" + "      },\n"
            + "      {\n"
            + "	selector: 'edge[type=\"transition\"]',\n" + "	style: {\n"
            + "	'width': 2,\n" + "	'target-arrow-shape': 'triangle',\n"
            + "	'line-color': '#d3d3d3',\n"
            + "	'target-arrow-color': '#d3d3d3',\n"
            + "	'curve-style': 'bezier',\n" + "	'content': 'data(label)',\n"
            + "	'edge-text-rotation': 'autorotate'\n" + "	}\n" + "      },\n"
            + "      ],\n" + "\n"
            + " layout: {\n"
            + " name: 'breadthfirst'\n"
            + "},\n";
    private final static String htmlTemplateBody2 = "  });\n"
            + "</script>\n"
            + "</body>";
    private final String directory;

    public ProofStructureHtmlExporter(String directory) {

        this.directory = directory;

    }

    @Override
    public void export(String name, ProofStructure ps) throws IOException {

        // Create a new file
        String filename = filePrefix + name + fileSuffix;
        File file = new File(directory);
        file.mkdirs();
        try {
            file.createNewFile();
        } catch (IOException e) {
            logger.debug("Not able to create file for proof structure visualisation: " + filename);
            throw e;
        }

        try {
            Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(directory
                            + File.separator + filename)));

            writer.write(htmlTemplateHead1);
            writer.write(filename);
            writer.write(htmlTemplateHead2);
            writer.write(htmlTemplateBody1);
            writer.write(psToJson(ps));
            writer.write(htmlTemplateBody2);
            writer.close();
        } catch (IOException ex) {
            logger.error("Unable to write to file: " + directory
                    + File.separator + filename);
        }

    }

    private String psToJson(ProofStructure ps) {

        StringBuilder psJSON = new StringBuilder("elements: [\n ");

        // Generate a unique mapping from assertions to ints
        HashMap<Assertion, Integer> nodeIds = new LinkedHashMap<>();

        // Initiate nodes section
        psJSON.append("//nodes \n");

        int i = 0;
        for (Assertion vertex : ps.getVertices()) {
            nodeIds.put(vertex, i);
            psJSON.append("{ data: { id: '");
            psJSON.append(i);
            if (vertex.isTrue()) {
                psJSON.append("', type: 'nodeSucc', ");
            } else {
                psJSON.append("', type: 'node', ");
            }
            psJSON.append("label: '");
            psJSON.append(vertex.stateIDAndFormulaeToString());
            psJSON.append("' } },\n");

            i++;
        }

        // Initiate edges section
        psJSON.append("//edges \n");
        for (Assertion source : ps.getVertices()) {
            Integer sourceID = nodeIds.get(source);
            if (!ps.getLeaves().contains(source)) {
                for (Assertion target : ps.getSuccessors(source)) {
                    Integer targetID = nodeIds.get(target);
                    psJSON.append("{data: {source: '");
                    psJSON.append(sourceID);
                    psJSON.append("', target: '");
                    psJSON.append(targetID);
                    psJSON.append("', ");
                    psJSON.append("type: 'transition' } },\n");
                }
            }
        }
        // Close elements section
        psJSON.append("],\n");

        return psJSON.toString();
    }


}
