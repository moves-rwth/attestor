package de.rwth.i2.attestor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.SootInitializer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import soot.*;
import soot.options.Options;

public class RecursiveMethodDetectionTest {

	@Ignore
	@Test
	public void test() {
		try {
			String classpath = "src\\test\\resources";
			new SootInitializer().initialize(classpath );
		

			Options.v().parse( new String [] { "-p", "jb",  "use-original-names:true" });

            /*
             This enables jimple annotations to find dead variables.
             Since dead variables may prevent abstraction, we need this information
             in order to delete them manually.
             */
			Options.v().parse( new String[]{"-p", "jap.lvtagger", "enabled:true"} );
		
			Options.v().parse( new String [] {"-pp", "-keep-line-number", "-f", "jimple", "RecursionDetectionInput" } );
			Scene.v().loadNecessaryClasses();

			PackManager.v().runPacks();


		} catch(Exception e) {
			throw e;
		}

		SootClass sootClass = Scene.v().getSootClass( "RecursionDetectionInput" );
		Scene.v().setMainClass( sootClass );
		
		TopLevelTranslation translator = new TopLevelTranslation( new StandardAbstractSemantics() );
		translator.translate();
		
		assertFalse( IpaAbstractMethod.getMethod("<RecursionDetectionInput: void <init>()>").isRecursive() );
		assertTrue( IpaAbstractMethod.getMethod("<RecursionDetectionInput: int indirectRecursion1()>").isRecursive());
		assertTrue( IpaAbstractMethod.getMethod("<RecursionDetectionInput: int indirectRecursion2(int)>").isRecursive());
		assertFalse(IpaAbstractMethod.getMethod("<RecursionDetectionInput: int callSeveral()>").isRecursive() );
		assertFalse( IpaAbstractMethod.getMethod("<RecursionDetectionInput: void main(String[])>").isRecursive() );
		assertFalse( IpaAbstractMethod.getMethod("<RecursionDetectionInput: int noCalls(int)>").isRecursive() );
		assertFalse( IpaAbstractMethod.getMethod("<RecursionDetectionInput: int nonRecursiveCaller()>").isRecursive() );
		assertFalse( IpaAbstractMethod.getMethod("<RecursionDetectionInput: boolean nonRecursiveCallee(boolean)>").isRecursive() );
		assertTrue( IpaAbstractMethod.getMethod("<RecursionDetectionInput: int selfLoop(int)>").isRecursive() );
		
	
	}

}
