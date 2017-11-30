package de.rwth.i2.attestor.io;

import de.rwth.i2.attestor.MockupSceneObject;
import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.graph.BasicNonterminal;
import de.rwth.i2.attestor.graph.BasicSelectorLabel;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.io.jsonImport.JsonToDefaultHC;

import de.rwth.i2.attestor.main.environment.SceneObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ParseTest {
	private static final Logger logger = LogManager.getLogger( "ParseTest.java" );


	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
	}

	@Test
	public void test(){

		SceneObject sceneObject = new MockupSceneObject();

		try{
			Scanner scan = new Scanner(new FileReader("src/test/resources/GraphEncodingTest.txt"));
			StringBuilder str = new StringBuilder();
			while (scan.hasNext())
				str.append(scan.nextLine());
			scan.close();

			// build a JSON object
			JSONObject obj = new JSONObject(str.toString());

			logger.trace( "length: " + obj.length() ); 
			logger.trace( "node number: " + obj.getJSONArray( "nodes" ).getJSONObject( 0 ).getInt( "number" ));

			/*
			 * store nonterminal for sake of testing. Normally the nonterminals should be created by reading a grammar
			 */
			BasicNonterminal.getNonterminal( "Hyperedge" , 3, new boolean[]{true,true,true} );

			JsonToDefaultHC importer = new JsonToDefaultHC(sceneObject);
			HeapConfiguration res = importer.jsonToHC( obj, s -> {} );

			logger.trace( "res:" + res );
			
			assertEquals("nr of nodes", 3, res.countNodes());
			assertEquals( "nr of externals", 2, res.countExternalNodes() );
			assertEquals( "nr of hyperedges", 1, res.countNonterminalEdges() );
			assertEquals( "nr of variables",  2, res.countVariableEdges() );
			assertEquals( "selector at 0", 1, res.selectorLabelsOf( res.externalNodeAt( 0 ) ).size() );
			assertEquals( "selector at 0 is next", 
					BasicSelectorLabel.getSelectorLabel("next"),
					res.selectorLabelsOf( res.externalNodeAt(0) ).get(0) );

		}catch( FileNotFoundException e ){
			e.printStackTrace();
			fail( "exception");
		}
	}
}
