package de.rwth.i2.attestor.io.htmlExport;

import java.io.*;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.graph.heap.*;
import gnu.trove.iterator.TIntIterator;

/**
 * All the dirty details to export a single HeapConfiguration into an HTML file.
 */
public class HeapConfigurationHtmlExporter implements HeapConfigurationExporter{

	private final static Logger logger = LogManager
			.getLogger( "HcToCytoscapeExporter" );

	private final static String filePrefix = "hc_";
	private final static String fileSuffix = ".html";

	private final List<String> exportedHcNames = new ArrayList<>();
	
	private final String directory;

	public HeapConfigurationHtmlExporter(String directory) {
		this.directory = directory;
	}

	@Override
	public void export( String name, HeapConfiguration hc ) {

		String filename = prepareExport( name );

		try {
			Writer writer = new BufferedWriter(
					new OutputStreamWriter( new FileOutputStream( directory
							+ File.separator + filename ) ) );

			writer.write( htmlTemplateHead );
            assert filename != null;
            writer.write( filename );
			writer.write( htmlTemplateBody );
			writer.write( hcToJson( hc ) );
			writer.write( htmlTemplateFooter );
			writer.close();
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

	private String hcToJson( HeapConfiguration hc ) {

		StringBuilder result = new StringBuilder( "elements: {\n nodes:[\n" );

		TIntIterator nodeIter = hc.nodes().iterator();
		while(nodeIter.hasNext()) {
			int node = nodeIter.next();

			result.append( "{ data: { id: '" );
			result.append( node );
			result.append( "', type: " );

			if( hc.isExternalNode(node) ) {
				result.append( "'external', label: '" );
				result.append(node).append(":").append(hc.externalIndexOf(node));
				result.append( "' } },\n" );
			} else {
				result.append( "'node' } },\n" );
			}
		}
		
		TIntIterator ntIter = hc.nonterminalEdges().iterator();
		while(ntIter.hasNext()) {
			
			int edge = ntIter.next();
			
			result.append( "{ data: { id: '" );
			result.append( edge );
			result.append( "', type: 'hyperedge', label: '" );
			result.append(edge).append(" : ").append(hc.labelOf(edge).toString());
			result.append( "' } },\n" );
		}

		TIntIterator varIter = hc.variableEdges().iterator();
		while(varIter.hasNext()) {
			
			int var = varIter.next();

			result.append( "{ data: { id: '" );
			result.append( var );
			result.append( "', type: 'variable', label: '" );
			result.append( hc.nameOf(var) );
			result.append( "' } },\n" );
		}

		result.append( "	],\n" );
		result.append( "edges: [\n" );

		nodeIter = hc.nodes().iterator();
		while(nodeIter.hasNext()) {
			
			int node = nodeIter.next();
			
			for( SelectorLabel sel : hc.selectorLabelsOf(node) ) {
				result.append( "		{ data: { source: '" );
				result.append( node );
				result.append( "', target: '" );
				
				int target = hc.selectorTargetOf(node, sel)  ;
				result.append( target );
				
				result.append( "', label: '" );
				result.append( sel.toString() );
				result.append( "', type: 'selector' } },\n" );
			}
		}
		
		ntIter = hc.nonterminalEdges().iterator();
		while(ntIter.hasNext()) {
			
			int edge = ntIter.next();
			
			int tentacle = 0;
			
			TIntIterator attIter = hc.attachedNodesOf(edge).iterator();
			while(attIter.hasNext()) {
				
				int node = attIter.next();
				
				result.append( "		{ data: { source: '" );
				result.append( edge );
				result.append( "', target: '" );
				result.append( node );
				result.append( "', label: '" );
				result.append( tentacle++ );
				result.append( "', type: 'tentacle' } },\n" );
			}
		}

		varIter = hc.variableEdges().iterator();
		while(varIter.hasNext()) {
			
			int var = varIter.next();
			
			result.append( "		{ data: { source: '" );
			result.append( var );
			result.append( "', target: '" );
			result.append( hc.targetOf(var) );
			result.append( "', label: '" );
			result.append( var );
			result.append( "', type: 'variable' } },\n" );
		}

		result.append( "	]\n" );
		result.append( "},\n" );

		return result.toString();
	}

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
			+ "\n" + "\n" + "	\n" + "		\n" + "		<style>\n" + "			body {\n"
			+ "				font-family: helvetica;\n" + "				font-size: 14px;\n"
			+ "			}\n" + "\n" + "			#cy {\n" + "				width: 100%;\n"
			+ "				height: 100%;\n" + "				position: absolute;\n"
			+ "				left: 0;\n" + "				top: 0;\n" + "				z-index: 999;\n"
			+ "			}\n" + "\n" + "			h1 {\n" + "				opacity: 0.5;\n"
			+ "				font-size: 1em;\n" + "			}\n" + "		</style>\n" + "		\n"
			+ "<script>\n" + "$(function(){  \n"
			+ "    var cy = window.cy = cytoscape({\n"
			+ "      container: document.getElementById('cy'),\n"
			+ "      boxSelectionEnabled: false,\n"
			+ "      autounselectify: true,\n" 
			+ "      layout: {\n"
			+ "	name: 'dagre',\n" 
		    + " rankDir: 'TB',\n"
		    + " zoom: 1,\n"
			+ "      },\n" 
		    + "\n" + "      style: [\n"
			+ "      {\n" + "	selector: 'node[type=\"node\"]',\n"
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

	private final static String htmlTemplateFooter = "  });\n" + "});\n"
			+ "</script>\n" + "</head>\n" + "\n" + "<body>\n"
			+ "  <div id=\"cy\"></div>\n" + "</body>\n" + "\n" + "</html>\n";

}
