package de.rwth.i2.attestor.io.htmlExport;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationExporter;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.stateSpaceGeneration.*;

/**
 * All the dirty details to export a full StateSpace into multiple HTML files.
 */
public class StateSpaceHtmlExporter implements StateSpaceExporter {

	private static final Logger logger = LogManager.getLogger( "StateSpaceHtmlExporter" );

	private final static String filePrefix = "stsp_";
	private final static String fileSuffix = ".html";
	private final static String navigationName = "StateSpaceNavigation.html";
	private final static String indexName = "StateSpaceIndex.html";

	private final List<String> exportedHcNames = new ArrayList<>();

	private final String directory;

	public StateSpaceHtmlExporter( String directory) {
		this.directory = directory;
	}


    private void createIndex() {

		StringBuilder builder = new StringBuilder();

		for( int i = 0; i < exportedHcNames.size(); i++ ) {
			builder.append( "<p><a href=\"" );
			builder.append( filePrefix );
			builder.append( i + 1 );
			builder.append( fileSuffix );
			builder.append( "\" target=\"graph\">" );
			builder.append( exportedHcNames.get( i ) );
			builder.append( "</a></p>\n" );
		}

		try {
			Writer writer = new BufferedWriter( new OutputStreamWriter(
					new FileOutputStream( directory + File.separator
							+ navigationName ) ) );

			writer.write( builder.toString() );
			writer.close();
		} catch( IOException ex ) {
			logger.error( "Unable to write to file: " + directory
					+ File.separator + navigationName );
		}

		try {
			Writer writer = new BufferedWriter( new OutputStreamWriter(
					new FileOutputStream( directory + File.separator
							+ indexName ) ) );

			writer.write( htmlIndex );
			writer.close();
		} catch( IOException ex ) {
			logger.error( "Unable to write to file: " + directory
					+ File.separator + indexName );
		}

		logger.info( "HeapConfigurations exported to " + directory
				+ File.separator + indexName );
	}

	public void export( String name, StateSpace stateSpace ) {


		String filename = prepareExport( name );

		try {
			Writer writer = new BufferedWriter(
					new OutputStreamWriter( new FileOutputStream( directory
							+ File.separator + filename ) ) );

			writer.write( htmlTemplateHead );
            assert filename != null;
            writer.write( filename );
			writer.write( htmlTemplateBody );
			writer.write( stateSpaceToJson( stateSpace ) );
			writer.write( htmlTemplateFooter );
			writer.close();

			HeapConfigurationExporter exporter =
					Settings.getInstance().factory().getHeapConfigurationExporter(
							directory
					);
			int id = 0;

			for( ProgramState state : stateSpace.getStates() ) {
				
				HeapConfiguration heap = state.getHeap();

				exporter.export( filename + "_" + id, heap );
				id++;	
			}

			createIndex();

		} catch( IOException ex ) {
			logger.error( "Unable to write to file: " + directory
					+ File.separator + filename );
		}


	}

	private String prepareExport( String name ) {
		exportedHcNames.add( name );

		String filename = nextFilename();

		File file = new File( directory );
		if( !file.exists() || !file.isDirectory() ) {
			boolean success = ( new File( directory ) ).mkdirs();
			if( !success ) {
				logger.error( "Unable to generate directory: " + directory );
				return null;
			}
		}
		return filename;
	}

	private String nextFilename() {

		return filePrefix + String.valueOf( exportedHcNames.size() )
				+ fileSuffix;
	}

	/**
	 * do not use from outside! Public only for testing.
	 * @param stateSpace the state space to encode
	 * @return a string encoding the json-object
	 */
	public static String stateSpaceToJson( StateSpace stateSpace ) {

		StringBuilder result = new StringBuilder( "elements: {\n nodes:[\n" );

		
		
		int id = 0;

        Map<ProgramState, Integer> stateMapping = new HashMap<>();

		for( ProgramState state : stateSpace.getStates() ) {

			result.append( "{ data: { id: '" );
			result.append( id );
			
			
			
			result.append( "', type: " );

			if(stateSpace.getFinalStates().contains(state)) {
				result.append( "'external' } },\n" );	
			} else {
				result.append( "'state' } },\n" );
			}
			
			stateMapping.put( state, id ); 
			++id;
		}

		result.append( "	],\n" );
		result.append( "edges: [\n" );

		for( Map.Entry<?,?> transition : stateSpace.getSuccessors().entrySet() ) {
			
			ProgramState predState = (ProgramState) transition.getKey();
			List<?> succList = (List<?>) transition.getValue();
			for(Object succ : succList) {
				
				StateSuccessor stateSucc = (StateSuccessor) succ;
				
				result.append( "		{ data: { source: '" );
				result.append( stateMapping.get( predState ) );
				result.append( "', target: '" );
				
				Integer target = stateMapping.get( stateSucc.getTarget() );
				if(target == null) {
					target = stateSpace.getStates().lastIndexOf(stateSucc.getTarget());
				}
				
				result.append( target );
				result.append( "', label: '" );
				result.append( stateSucc.getLabel() );
				result.append( "', type: 'selector' } },\n" );
			}
		}

		result.append( "	]\n" );
		result.append( "},\n" );

		return result.toString();
	}

	private final static String htmlIndex = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Frameset//EN\"\n"
			+ "  \"http://www.w3.org/TR/html4/frameset.dtd\">\n"
			+ "<html>\n"
			+ "  <head>\n"
			+ "    <meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\">\n"
			+ "    <title>Attestor Heap Configurations</title>\n"
			+ "  </head>\n"
			+ "  <frameset rows=\"30%, *\">\n"
			+ "    <frame src=\""
			+ "stsp_1.html"
			+ "\" name=\"navigation\">\n"
			+ "    <frame id=\"hc_graph\" src=\"hc_1.html\" name=\"graph\">\n"
			+ "  </frameset>\n" + "</html>";

	private final static String htmlTemplateHead = "<!DOCTYPE>\n" + "\n"
			+ "<html>\n" + "\n" + "	<head>\n" + "		<title>";

	private final static String htmlTemplateBody = "</title>\n"
			+ "\n"
			+ "		<meta name=\"viewport\" content=\"width=device-width, user-scalable=no, initial-scale=1, maximum-scale=1\">\n"
			+ "\n"
			+ "		<script src=\"http://code.jquery.com/jquery-2.0.3.min.js\"></script>\n"
			+ "		<script src=\"https://cdnjs.cloudflare.com/ajax/libs/cytoscape/3.1.2/cytoscape.min.js\"></script>\n"
			+ "\n"
			+ "		<!-- for testing with local version of cytoscape.js -->\n"
			+ "		<!--<script src=\"../cytoscape.js/build/cytoscape.js\"></script>-->\n"
			+ "\n"
			+ "		<script src=\"https://cdn.rawgit.com/cpettitt/dagre/v0.7.4/dist/dagre.min.js\"></script>\n"
			+ "		<script src=\"https://cdn.rawgit.com/cytoscape/cytoscape.js-dagre/1.5.0/cytoscape-dagre.js\"></script>\n"
			+ "\n" + "\n" + "\n" + "\n" + "		<style>\n" + "			body {\n"
			+ "				font-family: helvetica;\n" + "				font-size: 14px;\n"
			+ "			}\n" + "\n" + "			#cy {\n" + "				width: 100%;\n"
			+ "				height: 100%;\n" + "				position: absolute;\n"
			+ "				left: 0;\n" + "				top: 20px;\n" + "				z-index: 999;\n"
			+ "			}\n" + "        #hc_graph {\n"
			+ "	                width: 45%;\n"
			+ "	                height: 90%;\n"
			+ "	                position: absolute;\n"
			+ "                  }\n" + "\n" + "       .innerFrame {\n"
			+ "                	width : 100%;\n"
			+ "	                height: 100%;\n" + "                  }\n"
			+ "\n" + "			h1 {\n" + "				opacity: 0.5;\n"
			+ "				font-size: 1em;\n" + "			}\n" + "		</style>\n" + "		\n"
			+ "<script>\n" + "$(function(){  \n"
			+ "    var cy = window.cy = cytoscape({\n"
			+ "      container: document.getElementById('cy'),\n"
			+ "      boxSelectionEnabled: false,\n"
			+ "      autounselectify: true,\n" 
			+ "      layout: {\n"
			+ "	name: 'dagre',\n" 
		    + " rankDir: 'LR',\n"
		    + " nodeSep: 150,\n" 
		    + " edgeSep: 150,\n" 
		    + " rankSep: 300,\n" 
			+ "      },\n" 
			+ "\n"
			+ "      style: [\n"
			+ "      {\n" + "	selector: 'node[type=\"state\"]',\n"
			+ "	style: {\n" + "	  'content': 'data(id)',\n"
			+ "	  'text-opacity': 0.5,\n" + "	  'text-valign': 'center',\n"
			+ "	  'text-halign': 'center',\n"
			+ "	  'background-color': '#d3d3d3',\n" + "	  'shape' : 'circle'\n"
			+ "	}\n" + "      },\n" + "      {\n"
			+ "	selector: 'node[type=\"external\"]',\n" + "	style: {\n"
			+ "	  'content': 'data(label)',\n" + "	  'text-opacity': 0.5,\n"
			+ "	  'text-valign': 'center',\n" + "	  'text-halign': 'center',\n"
			+ "	  'background-color': '#ff0000',\n" + "	  'shape' : 'circle'\n"
			+ "	}\n" + "      },\n" + "      {\n"
			+ "	selector: 'node[type=\"hyperedge\"]',\n" + "	style: {\n"
			+ "	  'content': 'data(label)',\n" + "	  'text-opacity': 0.5,\n"
			+ "	  'text-valign': 'center',\n" + "	  'text-halign': 'center',\n"
			+ "	  'background-color': '#00FF00',\n"
			+ "	  'shape': 'rectangle'\n" + "	}\n" + "      },\n" + "      {\n"
			+ "	selector: 'node[type=\"variable\"]',\n" + "	style: {\n"
			+ "	  'content': 'data(label)',\n" + "	  'text-opacity': 0.5,\n"
			+ "	  'text-valign': 'center',\n" + "	  'text-halign': 'center',\n"
			+ "	  'background-color': '#6699FF',\n"
			+ "	  'shape': 'rectangle'\n" + "	}\n" + "      },\n" + "      {\n"
			+ "	selector: 'edge[type=\"selector\"]',\n" + "	style: {\n"
			+ "	'width': 2,\n" + "	'target-arrow-shape': 'triangle',\n"
			+ "	'line-color': '#d3d3d3',\n"
			+ "	'target-arrow-color': '#d3d3d3',\n"
			+ "	'curve-style': 'bezier',\n" + "	'content': 'data(label)',\n"
			+ "	'edge-text-rotation': 'autorotate'\n" + "	}\n" + "      },\n"
			+ "      {\n" + "	selector: 'edge[type=\"tentacle\"]',\n"
			+ "	style: {\n" + "	'width': 2,\n"
			+ "	'target-arrow-shape': 'none',\n"
			+ "	'line-color': '#00FF00',\n"
			+ "	'target-arrow-color': '#00FF00',\n"
			+ "	'curve-style': 'bezier',\n" + "	'content' : 'data(label)',\n"
			+ "	'edge-text-rotation': 'autorotate'\n" + "	}\n" + "      },\n"
			+ "      {\n" + "	selector: 'edge[type=\"variable\"]',\n"
			+ "	style: {\n" + "	'width': 2,\n"
			+ "	'target-arrow-shape': 'none',\n"
			+ "	'line-color': '#6699FF',\n"
			+ "	'target-arrow-color': '#6699FF',\n"
			+ "	'curve-style': 'bezier',\n" + "	}\n" + "      },\n"
			+ "      ],\n" + "\n";

	private final static String htmlTemplateFooter = "}); \n"
			+ " cy.on('click', 'node',  function(evt){\n"
			+ "var i = 1 + parseInt(this.id());\n"
			+ "parent.frames[1].location = \"hc_\" + i +\".html\";\n"
			+ "cy.fit(this.neighborhood());\n"
			+ "  });\n" + "});\n" + "</script>\n" + "</head>\n" + "\n"
			+ "<body>\n" 
			+ "<h1 align=\"right\">Generated State Space </h1>\n"
			+ "  <div id=\"cy\"></div>\n"
			+ "<div id='hc_graph'></div>\n" + "</body>\n" + "\n" + "</html>\n";

}
