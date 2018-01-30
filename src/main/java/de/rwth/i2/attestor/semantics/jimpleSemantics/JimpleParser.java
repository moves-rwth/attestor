package de.rwth.i2.attestor.semantics.jimpleSemantics;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.main.scene.ElementNotPresentException;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.ProgramParser;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.JimpleToAbstractSemantics;
import de.rwth.i2.attestor.semantics.jimpleSemantics.translation.TopLevelTranslation;
import de.rwth.i2.attestor.stateSpaceGeneration.Program;
import soot.*;
import soot.options.Options;

/**
 * A parser that takes a source code file and transforms it first into the Jimple intermediate language
 * and then into our own semantics; that is it constructs a {@link Program}.
 *
 * @author Hannah Arndt, Christoph
 */
public class JimpleParser extends SceneObject implements ProgramParser {

    /**
     * The logger of this parser.
     */
    private static final Logger logger = LogManager.getLogger("BytecodeParser");

    /**
     * The underlying translation of Jimple objects to our own semantics objects.
     */
    private final JimpleToAbstractSemantics translationDef;

    /**
     * Creates a new parser.
     *
     * @param translationDef The underlying translation of Jimple objects to our own semantics objects.
     */
    public JimpleParser(SceneObject sceneObject, JimpleToAbstractSemantics translationDef) {

        super(sceneObject);
        this.translationDef = translationDef;
    }

    @Override
    public Program parse(String classpath, String classname, String entryPoint) {

        try {
            logger.debug("Initializing Soot with classpath: " + classpath);
            new SootInitializer().initialize(classpath);


            Options.v().parse(new String[]{"-p", "jb", "use-original-names:true"});

            /*
             This enables jimple annotations to find dead variables.
             Since dead variables may prevent abstraction, we need this information
             in order to delete them manually.
             */
            Options.v().parse(new String[]{"-p", "jap.lvtagger", "enabled:true"});

            Options.v().parse(new String[]{"-pp", "-keep-line-number", "-f", "jimple", classname});
            Scene.v().loadNecessaryClasses();

            logger.info("Invoking Soot...");
            PackManager.v().runPacks();


        } catch (Exception e) {

            logger.fatal("Soot threw an exception.");
            throw e;
        }

        logger.trace("start translating");

        SootClass sootClass = Scene.v().getSootClass(classname);
        Scene.v().setMainClass(sootClass);

        TopLevelTranslation translator = new TopLevelTranslation(this, translationDef);
        translator.translate();

        String mainMethodName = sootClass.getMethodByName(entryPoint).getSignature();

        try {
			return scene().getMethodIfPresent(mainMethodName).getBody();
		} catch (ElementNotPresentException e) {
			logger.fatal("Translation did not generate main method");
			throw new IllegalStateException();
		}
    }
}
