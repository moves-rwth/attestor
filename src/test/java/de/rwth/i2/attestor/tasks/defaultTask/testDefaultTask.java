package de.rwth.i2.attestor.tasks.defaultTask;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.AnalysisTaskBuilder;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;
import de.rwth.i2.attestor.tasks.GeneralSelectorLabel;
import org.junit.BeforeClass;
import org.junit.Test;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.util.Chain;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.*;

public class testDefaultTask{
	//private static final Logger logger = LogManager.getLogger( "testDefaultTask" );


	@BeforeClass
	public static void init()
	{
		UnitTestGlobalSettings.reset();
	}

	@Test
	public void testLoad() {

		AnalysisTaskBuilder builder = new DefaultAnalysisTaskBuilder();

	    GeneralNonterminal.getNonterminal("Hyperedge", 3, new boolean[]{true,true,true});

		try {

            final String examplesDirectory = "src" + File.separator + "test" + File.separator + "resources";
			Settings.getInstance().grammar().loadGrammarFromFile(examplesDirectory + File.separator + "grammarEncodingTest.txt", null );

			builder.loadInput( examplesDirectory + File.separator + "GraphEncodingTest.txt" );

			builder.loadProgram( examplesDirectory, "List", "main" );
			SootClass sootClass = Scene.v().getSootClass( "List" );

			AnalysisTask task = builder.build();

			HeapConfiguration res = task.getInput();

			assertEquals("nr of nodes", 6, res.countNodes());
			assertEquals( "nr of externals", 2, res.countExternalNodes() );
			assertEquals( "nr of hyperedges", 1, res.countNonterminalEdges() );
			assertEquals( "nr of variables",  7, res.countVariableEdges() );
			int node = res.externalNodeAt(0);
			assertEquals( "selector at 0", 1, res.selectorLabelsOf(node).size() );
			assertEquals( "selector at 0 is next", 
					GeneralSelectorLabel.getSelectorLabel("next"),
					res.selectorLabelsOf(node).iterator().next() );


			assertEquals( "number of fields", 1, sootClass.getFieldCount() );
			assertEquals( "List", sootClass.getName() );
			SootMethod method = sootClass.getMethods().get( 0 );
			Chain<Unit> units = method.getActiveBody().getUnits();
			Unit curr = units.getFirst();

			//test whether line numbers are supported
			for( int i = 0; i < units.size(); i++ ){
				assertTrue( curr.getJavaSourceStartLineNumber() != -1 );
				curr = units.getSuccOf( curr );
			}


			Grammar resultGrammar = Settings.getInstance().grammar().getGrammar();
			GeneralNonterminal nt = GeneralNonterminal.getNonterminal("DLList");

			assertTrue( "resultGrammar has nonterminal", resultGrammar.getAllLeftHandSides().contains( nt  ));
			assertEquals( "number of rules in resultGrammar", 3, resultGrammar.getRightHandSidesFor( nt ).size());
			assertEquals( "rank", 2, nt.getRank() );
			assertFalse( nt.isReductionTentacle( 0 ));
			assertFalse( nt.isReductionTentacle( 1 ) );

		} catch( FileNotFoundException e ) {
			fail("Unexpected exception: " + e.getMessage() );
		}

	}

}
