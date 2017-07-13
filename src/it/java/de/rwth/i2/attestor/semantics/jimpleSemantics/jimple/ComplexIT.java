package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;

import de.rwth.i2.attestor.IntegrationTestGlobalSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.HeapConfiguration;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.SootInitializer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import de.rwth.i2.attestor.stateSpaceGeneration.*;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultAnalysisTask;
import soot.*;
import soot.options.Options;

public class ComplexIT {
	private static final Logger logger = LogManager.getLogger( "testStateSpaceGenerator.java" );

	@Test
	public void testStatic() {

		String name = "staticTest";

		String classname = TestWithStatic.class.getName();
		// System.out.println("partial Path: " +
		// EasyList.class.getName().replace(".", "/"));
		String classpath = TestWithStatic.class
				.getResource( TestWithStatic.class.getSimpleName() + ".class" )
				.getPath()
				.replace(
						TestWithStatic.class.getName().replace( ".", "/" )
								+ ".class", "" );

		StateSpace stateSpace = executeTest( name, classname, classpath );
		
		List<ProgramState> states = stateSpace.getStates();
		
		assertTrue(15 < states.size() && states.size() < 18);
		assertEquals("expected res 0", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 0 ).getHeap() );
		assertEquals("expected res 1", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 1 ).getHeap() );

	    //variables do not match
		//assertEquals("expected res 16", ExampleHcImplFactory.expectedResStaticList(), states.get(15).getHeap() );
		//assertEquals("expected res 15", ExampleHcImplFactory.expectedResStaticList_beforeReturn(), states.get(14).getHeap() );
		//assertEquals("expected res 14", ExampleHcImplFactory.expectedResStaticList(), states.get(13).getHeap() );
		
		//only controlFlowEdges without branches
		for( Entry<ProgramState, List<StateSuccessor>> pair : stateSpace.getSuccessors().entrySet() ){
			assertEquals( 1, pair.getValue().size() );
			assertTrue(!pair.getValue().get(0).getLabel().equals(""));
		}
		assertEquals( 1, stateSpace.getFinalStates().size() );
		assertEquals("expected res terminal state", ExampleHcImplFactory.expectedResStaticList(), stateSpace.getFinalStates().get( 0 ).getHeap());

	}

	@Test
	public void testEasyList() {

		String name = "EasyList";

		String classname = EasyList.class.getName();
		// System.out.println("partial Path: " +
		// EasyList.class.getName().replace(".", "/"));
		String classpath = EasyList.class
				.getResource( EasyList.class.getSimpleName() + ".class" )
				.getPath()
				.replace(
						EasyList.class.getName().replace( ".", "/" ) + ".class",
						"" );

		StateSpace stateSpace = executeTest( name, classname, classpath );
		
		List<ProgramState> states = stateSpace.getStates();
		assertTrue( "number of states", 24 < states.size() && states.size() < 27 );
		assertEquals( "heap at 0", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 0 ).getHeap() );
		assertEquals( "heap at 1", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 1 ).getHeap() );
		
        //assertEquals( "heap at 24",  ExampleHcImplFactory.expectedResultEasyList(), states.get(24).getHeap() );
		assertEquals( "heap at 23", ExampleHcImplFactory.expectedResultEasyList_beforeReturn(), states.get(23).getHeap() );
		//assertEquals( "heap at 22", ExampleHcImplFactory.expectedResultEasyList(), states.get(22).getHeap() );
		
		//only controlFlowEdges without branches
		for( Entry<ProgramState, List<StateSuccessor>> pair : stateSpace.getSuccessors().entrySet() ){
			assertEquals( 1, pair.getValue().size() );
			assertTrue(!Objects.equals(pair.getValue().get(0).getLabel(), ""));
		}
		assertEquals("number of terminal states",  1, stateSpace.getFinalStates().size() );
		assertEquals( "heap at terminal state", ExampleHcImplFactory.expectedResultEasyList(), stateSpace.getFinalStates().get( 0 ).getHeap());
	}

	@Test
	public void testNormalList() {
		String name = "NormalList";
		String classname = NormalList.class.getName();
		// System.out.println("partial Path: " +
		// EasyList.class.getName().replace(".", "/"));
		String classpath = NormalList.class
				.getResource( NormalList.class.getSimpleName() + ".class" )
				.getPath()
				.replace(
						NormalList.class.getName().replace( ".", "/" )
								+ ".class", "" );

		StateSpace stateSpace = executeTest( name, classname, classpath );
		
		List<ProgramState> states = stateSpace.getStates();
		
		assertTrue( states.size() >= 19 && states.size() <= 20 );
		assertEquals("expected res 0", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 0 ).getHeap() );
		assertEquals("expected res 1", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 1 ).getHeap() );

        // variables do not match
		//assertEquals("expected res 18", ExampleHcImplFactory.expectedResNormalList(), states.get(18).getHeap() );

        // variables do not match
		//assertEquals("expected res 17", ExampleHcImplFactory.expectedResNormalList_beforeReturn(), states.get(17).getHeap() );

		// variables do not match
		//assertEquals("expected res 16", ExampleHcImplFactory.expectedResNormalList(), states.get(16).getHeap() );
	
		//only controlFlowEdges without branches
		for( Entry<ProgramState, List<StateSuccessor>> pair : stateSpace.getSuccessors().entrySet() ){
			assertEquals( 1, pair.getValue().size() );
			assertTrue(!Objects.equals(pair.getValue().get(0).getLabel(), ""));
		}
		assertEquals( 1, stateSpace.getFinalStates().size() );
		assertEquals("expected res terminal state", ExampleHcImplFactory.expectedResNormalList(), stateSpace.getFinalStates().get( 0 ).getHeap());
	
	}

	@Test
	public void testRecursion() {

		String name = "NegTest";
		String classname = BoolList.class.getName();
		// System.out.println("partial Path: " +
		// EasyList.class.getName().replace(".", "/"));
		String classpath = BoolList.class
				.getResource( BoolList.class.getSimpleName() + ".class" )
				.getPath()
				.replace(
						BoolList.class.getName().replace( ".", "/" ) + ".class",
						"" );

		
		StateSpace stateSpace = executeTest( name, classname, classpath );
		
		List<ProgramState> states = stateSpace.getStates();
		assertTrue( 9 < states.size() && states.size() < 12 );
		assertEquals("expected res 0", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 0 ).getHeap() );
		assertEquals("expected res 1", ExampleHcImplFactory.getEmptyGraphWithConstants(), states.get( 1 ).getHeap() );
		
		
		//assertEquals("expected res 9", ExampleHcImplFactory.expectedResBoolList(), states.get(9).getHeap() );
		//assertEquals("expected res 8", ExampleHcImplFactory.expectedResBoolList(), states.get(8).getHeap() );

		//only controlFlowEdges without branches
		for( Entry<ProgramState, List<StateSuccessor>> pair : stateSpace.getSuccessors().entrySet() ){
			//assertEquals( 1, pair.getValue().size() );
			assertTrue(!Objects.equals(pair.getValue().get(0).getLabel(), ""));
		}
		assertEquals( 1, stateSpace.getFinalStates().size() );
		//assertEquals("expected res terminal state", ExampleHcImplFactory.expectedResBoolList(), stateSpace.getFinalStates().get( 0 ).getHeap());
	

	}

	private StateSpace executeTest( String name, String classname, String classpath ) {

		logger.trace( "Invoking ProgramReader: " + classname + ", " + classpath );
		new SootInitializer().initialize(classpath);
//
		logger.trace( "loading soot" );

		Options.v().parse( new String[] { "-f", "jimple", classname } );
		Scene.v().loadNecessaryClasses();
		PackManager.v().runPacks();

		logger.trace( "start parsing" );

		SootClass sootClass = Scene.v().getSootClass( classname );
		Scene.v().setMainClass( sootClass );

		TopLevelTranslation translation = new TopLevelTranslation();
		
		translation.translate();

		String mainMethodName = sootClass.getMethodByName( "main" ).getSignature();

		//String mainMethodName = Scene.v().getMainClass().getMethodByName( "main" ).getSignature();

		Program mainProgram = translation.getMethod(mainMethodName).getControlFlow();

		HeapConfiguration initialHeap = ExampleHcImplFactory.getEmptyGraphWithConstants();
		Grammar grammar = Grammar.builder().build();

        Settings.getInstance().grammar().setGrammar(grammar);

		AnalysisTask task = DefaultAnalysisTask.builder()
				.setProgram(mainProgram)
				.setInput(initialHeap)
				.build();

		task.execute();
		logger.trace( task.getStateSpace() );


	    Settings.getInstance().factory()
                .getStateSpaceExporter(IntegrationTestGlobalSettings.getExportPath(name))
                .export("stateSpace", task.getStateSpace());

		return task.getStateSpace();
	}

}
