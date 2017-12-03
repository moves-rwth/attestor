package de.rwth.i2.attestor;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import de.rwth.i2.attestor.main.environment.SceneObject;
import org.junit.Test;

import de.rwth.i2.attestor.ipa.IpaAbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.SootInitializer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import soot.*;
import soot.options.Options;

public class RecursiveMethodDetectionTest {

	@Test
	public void test() {

		SceneObject sceneObject = new MockupSceneObject();

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

		TopLevelTranslation translator = new TopLevelTranslation(sceneObject,  new StandardAbstractSemantics(sceneObject) );
		translator.translate();
		
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: void <init>()>").isRecursive() );
		assertTrue(sceneObject.scene().getMethod("<RecursionDetectionInput: int indirectRecursion1()>").isRecursive());
		assertTrue(sceneObject.scene().getMethod("<RecursionDetectionInput: int indirectRecursion2(int)>").isRecursive());
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: int callSeveral()>").isRecursive() );
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: void main(String[])>").isRecursive() );
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: int noCalls(int)>").isRecursive() );
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: int nonRecursiveCaller()>").isRecursive() );
		assertFalse(sceneObject.scene().getMethod("<RecursionDetectionInput: boolean nonRecursiveCallee(boolean)>").isRecursive() );
		assertTrue(sceneObject.scene().getMethod("<RecursionDetectionInput: int selfLoop(int)>").isRecursive() );
		
	
	}

}
