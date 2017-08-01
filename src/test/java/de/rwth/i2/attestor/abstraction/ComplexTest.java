package de.rwth.i2.attestor.abstraction;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.rwth.i2.attestor.UnitTestGlobalSettings;
import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import de.rwth.i2.attestor.abstraction.programs.LongList;
import de.rwth.i2.attestor.grammar.Grammar;
import de.rwth.i2.attestor.graph.heap.internal.ExampleHcImplFactory;
import de.rwth.i2.attestor.main.AnalysisTask;
import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.SootInitializer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpace;
import de.rwth.i2.attestor.tasks.GeneralNonterminal;
import de.rwth.i2.attestor.tasks.defaultTask.DefaultAnalysisTask;
import soot.*;
import soot.options.Options;

public class ComplexTest {
	private static final Logger logger = LogManager.getLogger( "ComplexTest" );

    private static Grammar grammar;
	
	@BeforeClass
	public static void init() {

		UnitTestGlobalSettings.reset();
		
		GeneralNonterminal listLabel = GeneralNonterminal.getNonterminal( "List", 2, new boolean[] { false, true } );
		grammar = Grammar.builder()
				.addRule( listLabel, ExampleHcImplFactory.getLongListRule1() )
				.addRule( listLabel, ExampleHcImplFactory.getLongListRule2() )
				.addRule( listLabel, ExampleHcImplFactory.getLongListRule3() )
				.build();
	}

	@Test
	public void testLongList() {

		String name = "LongListTest";
		String classname = LongList.class.getName();
		String classpath = LongList.class
				.getResource( LongList.class.getSimpleName() + ".class" )
				.getPath()
				.replace(
						LongList.class.getName().replace( ".", "/" ) + ".class",
						"" );

		MockupAbortStrategy abortStrategy = new MockupAbortStrategy();
		
		executeTest( name, classname, classpath, abortStrategy );
		
		assertTrue("No abstraction happened.", abortStrategy.hasAbstractedSomeState());
		assertFalse("We should have constructed a much smaller state space", abortStrategy.hasReachedLimit());

	}

	private void executeTest( String name, String classname, String classpath, MockupAbortStrategy abortStrategy ) {
		
		logger.trace( "Invoking ProgramReader: " + classname + ", " + classpath );
		new SootInitializer().initialize(classpath);

		logger.trace( "loading soot" );

		Options.v().parse( new String[] { "-f", "jimple", classname } );
		Scene.v().loadNecessaryClasses();
		PackManager.v().runPacks();

		logger.trace( "start parsing" );

		SootClass sootClass = Scene.v().getSootClass( classname );
		Scene.v().setMainClass( sootClass );
		
		TopLevelTranslation translation = new TopLevelTranslation();
		translation.translate();

		String mainMethodName = sootClass
			.getMethodByName( "main" )
			.getSignature();

		Program mainProgram = translation
			.getMethod( mainMethodName )
			.getControlFlow();


		Settings.getInstance().grammar().setGrammar(grammar);

		AnalysisTask task = DefaultAnalysisTask.builder()
                .setAbortStrategy(abortStrategy)
				.setInput(ExampleHcImplFactory.getEmptyGraphWithConstants())
				.setProgram(mainProgram)
				.build();

		StateSpace stateSpace = task.execute();

		Settings.getInstance().factory()
                .getStateSpaceExporter( "target" + File.separator
						+ "it-output" + File.separator + name + "Execution")
		        .export( "stateSpace", stateSpace );
	}
	
}
