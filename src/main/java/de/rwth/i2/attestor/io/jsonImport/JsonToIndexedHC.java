package de.rwth.i2.attestor.io.jsonImport;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import de.rwth.i2.attestor.graph.SelectorLabel;
import de.rwth.i2.attestor.main.environment.SceneObject;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminalImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.Nonterminal;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.HeapConfigurationBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.programState.indexedState.AnnotatedSelectorLabel;
import de.rwth.i2.attestor.programState.indexedState.IndexedNonterminal;
import de.rwth.i2.attestor.programState.indexedState.index.*;
import de.rwth.i2.attestor.types.Type;
import gnu.trove.list.array.TIntArrayList;

/**
 * 
 * for the index symbols we assume the following:
 * IndexVariable: "()"
 * AbstractIndexSymbol: "_LABEL" (starts with underscore)
 * BottomSymbol: "Z" (starts with upper case letter)
 * normal Symbol: "s" (starts with lower case)
 * 
 * @author Hannah
 *
 */
public class JsonToIndexedHC extends SceneObject {

	public JsonToIndexedHC(SceneObject sceneObject) {
		super(sceneObject);
	}

	public HeapConfiguration jsonToHC( JSONObject obj, Consumer<String> addSelectorLabelFunction ) {

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

			Nonterminal nt;
			if( hyperedge.has("index") ){
				List<IndexSymbol> index = parseIndex( hyperedge.getJSONArray("index") );

				Nonterminal bnt = scene().getNonterminal(label);
				IndexedNonterminal indexedNt = new IndexedNonterminalImpl(bnt, index);
				nt = indexedNt.getWithIndex(index);
			}else{
				nt = scene().getNonterminal(label);
			}

			TIntArrayList tentacles = new TIntArrayList(nt.getRank());
			for( int tentacleNr = 0; tentacleNr < hyperedge.getJSONArray( "tentacles" ).length(); tentacleNr++){
				tentacles.add( nodes.get( hyperedge.getJSONArray( "tentacles" ).getInt( tentacleNr ) ) );
			}

			builder.addNonterminalEdge(nt, tentacles);
		}
	}

	List<IndexSymbol>  parseIndex(JSONArray index){
		List<IndexSymbol> res = new ArrayList<>();
		for( int i = 0; i < index.length(); i++ ){
			String symbol = index.getString(i);
			if( symbol.equals("()") ){
				res.add( IndexVariable.getIndexVariable() );

				assert( i == index.length() -1 ) : "variables should be the last symbol of a index";
			}else if( symbol.startsWith("_") ){
				res.add( AbstractIndexSymbol.get(symbol.substring(1)) );
				assert( i == index.length() -1 ) : "abstract index symbols may only occur at the end of index";
			}else if( Character.isLowerCase(symbol.codePointAt(0)) ){
				res.add( ConcreteIndexSymbol.getIndexSymbol(symbol, false) );
				assert( i < index.length() -1 ) : "indices cannot end with a concrete non-bottom symbol";
			}else if( Character.isUpperCase( symbol.codePointAt(0)) ){
				res.add( ConcreteIndexSymbol.getIndexSymbol(symbol, true) );

				assert( i == index.length() -1 ) : "bottom symbols have to be the last element of a index";
			}
		}
		return res;
	}

	private void parseSelectors(HeapConfigurationBuilder builder,
									   TIntArrayList nodes,
									   JSONArray selectors,
									   Consumer<String> addSelectorLabelFunction) {


		for( int i = 0; i < selectors.length(); i++ ){
			final JSONObject selectorInJson = selectors.getJSONObject(i);
			String name = selectorInJson.getString( "label" );
            
			String annotation = "";
			if( selectorInJson.has("annotation") ){
			annotation = selectorInJson.getString("annotation");
			}
            
			int originID = selectorInJson.getInt( "origin" );
			int targetID = selectorInJson.getInt( "target" );
            
			addSelectorLabelFunction.accept(name);
			SelectorLabel sel = scene().getSelectorLabel(name);
			builder.addSelector( nodes.get( originID ),
					new AnnotatedSelectorLabel(sel, annotation),
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
