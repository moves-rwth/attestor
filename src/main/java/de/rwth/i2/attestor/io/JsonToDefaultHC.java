package de.rwth.i2.attestor.io;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.graph.GeneralNonterminal;
import de.rwth.i2.attestor.graph.GeneralSelectorLabel;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import gnu.trove.list.array.TIntArrayList;

public class JsonToDefaultHC {
	
	@SuppressWarnings("unused")
	private static final Logger logger = LogManager.getLogger( "JsonToDefaultHC" );

	public static HeapConfiguration jsonToHC( JSONObject obj ) {
		
		HeapConfigurationBuilder builder = Settings.getInstance().factory().createEmptyHeapConfiguration().builder();

		JSONArray jsonNodes = obj.getJSONArray( "nodes" );
		TIntArrayList nodes = JsonToDefaultHC.parseNodes( builder, jsonNodes );
	
		JSONArray externals = obj.getJSONArray( "externals" );
		JsonToDefaultHC.parseExternals( builder, nodes, externals );
	
		JSONArray variables = obj.getJSONArray( "variables" );
		JsonToDefaultHC.parseVariables( builder, nodes, variables );
	
		JSONArray selectors = obj.getJSONArray( "selectors" );
		JsonToDefaultHC.parseSelectors( builder, nodes, selectors );
	
		JSONArray hyperedges = obj.getJSONArray( "hyperedges" );
		JsonToDefaultHC.parseHyperedges( builder, nodes, hyperedges );
	
		return builder.build();
	}

	private static void parseHyperedges( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray hyperedges ) {
		for( int i = 0; i < hyperedges.length(); i++ ){
			JSONObject hyperedge = hyperedges.getJSONObject( i );
			String label = hyperedge.getString( "label" );
			Nonterminal nt = GeneralNonterminal.getNonterminal(label);
			
			TIntArrayList tentacles = new TIntArrayList();
			for( int tentacleNr = 0; tentacleNr < hyperedge.getJSONArray( "tentacles" ).length(); tentacleNr++){
				tentacles.add( nodes.get( hyperedge.getJSONArray( "tentacles" ).getInt( tentacleNr ) ) );
			}
			
			builder.addNonterminalEdge(nt, tentacles);
		}
	}

	private static void parseSelectors( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray selectors ) {
		for( int i = 0; i < selectors.length(); i++ ){
			String name = selectors.getJSONObject( i ).getString( "label" );
			int originID = selectors.getJSONObject( i ).getInt( "origin" );
			int targetID = selectors.getJSONObject( i ).getInt( "target" );
			builder.addSelector( nodes.get( originID ), 
					GeneralSelectorLabel.getSelectorLabel(name),
					nodes.get( targetID ) );
		}
	}

	private static void parseVariables( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray variables ) {
		for( int i = 0; i < variables.length(); i++ ){
			String name = variables.getJSONObject( i ).getString( "name" );
			int targetId = variables.getJSONObject( i ).getInt( "target" );
			builder.addVariableEdge( name, nodes.get( targetId ) );
		}
	}

	private static void parseExternals( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray externals ) {
		for( int i = 0; i < externals.length(); i++ ){
			int nodeId = externals.getInt( i );
			builder.setExternal( nodes.get( nodeId ) );
		}
	}

	private static TIntArrayList parseNodes(HeapConfigurationBuilder builder, JSONArray jsonNodes ) {
		TIntArrayList nodes = new TIntArrayList();
		for( int i = 0; i < jsonNodes.length(); i++ ){
			String typeName = jsonNodes.getJSONObject( i ).getString( "type" );
			Type type = TypeFactory.getInstance().getType(typeName);
			int number = jsonNodes.getJSONObject( i ).getInt( "number" );
			builder.addNodes( type, number, nodes );
		}
		return nodes;
	}


}
