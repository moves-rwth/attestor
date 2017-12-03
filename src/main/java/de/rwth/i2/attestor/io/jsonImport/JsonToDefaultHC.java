package de.rwth.i2.attestor.io.jsonImport;


import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.function.Consumer;

public class JsonToDefaultHC extends SceneObject {

	public JsonToDefaultHC(SceneObject sceneObject) {
		super(sceneObject);
	}

	public HeapConfiguration jsonToHC(JSONObject obj, Consumer<String> addSelectorLabelFunction) {
		
		HeapConfigurationBuilder builder = scene().createHeapConfiguration().builder();

		JSONArray jsonNodes = obj.getJSONArray( "nodes" );
		TIntArrayList nodes = parseNodes( builder, jsonNodes );
	
		JSONArray externals = obj.getJSONArray( "externals" );
		parseExternals( builder, nodes, externals );
	
		JSONArray variables = obj.getJSONArray( "variables" );
		parseVariables( builder, nodes, variables );
	
		JSONArray selectors = obj.getJSONArray( "selectors" );
		parseSelectors( builder, nodes, selectors, addSelectorLabelFunction );
	
		JSONArray hyperedges = obj.getJSONArray( "hyperedges" );
		parseHyperedges( builder, nodes, hyperedges );
	
		return builder.build();
	}

	private void parseHyperedges( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray hyperedges ) {
		for( int i = 0; i < hyperedges.length(); i++ ){
			JSONObject hyperedge = hyperedges.getJSONObject( i );
			String label = hyperedge.getString( "label" );
			Nonterminal nt = scene().getNonterminal(label);
			
			TIntArrayList tentacles = new TIntArrayList();
			for( int tentacleNr = 0; tentacleNr < hyperedge.getJSONArray( "tentacles" ).length(); tentacleNr++){
				tentacles.add( nodes.get( hyperedge.getJSONArray( "tentacles" ).getInt( tentacleNr ) ) );
			}
			
			builder.addNonterminalEdge(nt, tentacles);
		}
	}

	private void parseSelectors( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray selectors, Consumer<String> addSelectorLabelFunction) {

		for( int i = 0; i < selectors.length(); i++ ){
			String name = selectors.getJSONObject( i ).getString( "label" );
			int originID = selectors.getJSONObject( i ).getInt( "origin" );
			int targetID = selectors.getJSONObject( i ).getInt( "target" );

			addSelectorLabelFunction.accept(name);
			builder.addSelector( nodes.get( originID ),
					scene().getSelectorLabel(name),
					nodes.get( targetID ) );
		}
	}

	private void parseVariables( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray variables ) {
		for( int i = 0; i < variables.length(); i++ ){
			String name = variables.getJSONObject( i ).getString( "name" );
			int targetId = variables.getJSONObject( i ).getInt( "target" );
			builder.addVariableEdge( name, nodes.get( targetId ) );
		}
	}

	private void parseExternals( HeapConfigurationBuilder builder,
			TIntArrayList nodes, JSONArray externals ) {
		for( int i = 0; i < externals.length(); i++ ){
			int nodeId = externals.getInt( i );
			builder.setExternal( nodes.get( nodeId ) );
		}
	}

	private TIntArrayList parseNodes(HeapConfigurationBuilder builder, JSONArray jsonNodes ) {
		TIntArrayList nodes = new TIntArrayList();
		for( int i = 0; i < jsonNodes.length(); i++ ){
			String typeName = jsonNodes.getJSONObject( i ).getString( "type" );
			Type type = scene().getType(typeName);
			int number = jsonNodes.getJSONObject( i ).getInt( "number" );
			builder.addNodes( type, number, nodes );
		}
		return nodes;
	}


}
