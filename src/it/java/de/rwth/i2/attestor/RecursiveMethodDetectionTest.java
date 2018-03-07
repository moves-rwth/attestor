package de.rwth.i2.attestor;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.SootInitializer;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.StandardAbstractSemantics;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import org.junit.Ignore;
import org.junit.Test;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecursiveMethodDetectionTest {

    @Ignore
    @Test
    public void test() {

        SceneObject sceneObject = new MockupSceneObject();

        String classpath = "src\\test\\resources";
        new SootInitializer().initialize(classpath);


        Options.v().parse(new String[]{"-p", "jb", "use-original-names:true"});

            /*
             This enables jimple annotations to find dead variables.
             Since dead variables may prevent abstraction, we need this information
             in order to delete them manually.
             */
        Options.v().parse(new String[]{"-p", "jap.lvtagger", "enabled:true"});

        Options.v().parse(new String[]{"-pp", "-keep-line-number", "-f", "jimple", "RecursionDetectionInput"});
        Scene.v().loadNecessaryClasses();

        PackManager.v().runPacks();


        SootClass sootClass = Scene.v().getSootClass("RecursionDetectionInput");
        Scene.v().setMainClass(sootClass);

        TopLevelTranslation translator = new TopLevelTranslation(sceneObject, new StandardAbstractSemantics(sceneObject));
        translator.translate();

        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: void <init>()>").isRecursive());
        assertTrue(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int indirectRecursion1()>").isRecursive());
        assertTrue(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int indirectRecursion2(int)>").isRecursive());
        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int callSeveral()>").isRecursive());
        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: void main(String[])>").isRecursive());
        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int noCalls(int)>").isRecursive());
        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int nonRecursiveCaller()>").isRecursive());
        assertFalse(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: boolean nonRecursiveCallee(boolean)>").isRecursive());
        assertTrue(sceneObject.scene().getOrCreateMethod("<RecursionDetectionInput: int selfLoop(int)>").isRecursive());


    }

}
