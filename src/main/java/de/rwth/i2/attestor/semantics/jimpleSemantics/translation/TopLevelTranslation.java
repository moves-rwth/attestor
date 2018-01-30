package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.main.scene.ElementNotPresentException;
import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.phases.symbolicExecution.stateSpaceGenerationImpl.ProgramImpl;
import de.rwth.i2.attestor.procedures.Method;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.SemanticsCommand;
import de.rwth.i2.attestor.types.Type;
import soot.*;
import soot.jimple.Stmt;
import soot.util.Chain;

/**
 * This class organizes the actual translation process by splitting a Jimple program
 * into methodExecution and methodExecution into units. The lower translation layers can ask
 * this element for the pc of a given unit and the method-translation for a
 * given method-signature.
 * <p>
 * The translation process is started by translate().
 * <p>
 * This class does not specify actual translation rules for statements/values/types. Those
 * can be found in the translation hierarchy starting in {@link #firstLevel}
 *
 * @author Hannah Arndt, Christoph
 */
public class TopLevelTranslation extends SceneObject implements JimpleToAbstractSemantics {

    /**
     * The logger for this class.
     */
    private static final Logger logger = LogManager.getLogger("TopLevelTranslation");
    /**
     * The next level in the translation hierarchy. This level is the first one
     * to actually translate statements/values/types.
     */
    private final JimpleToAbstractSemantics firstLevel;
    /**
     * Instance of Tarjan's Algorithm to find SCCs. Has to be filled with all call-edges
     * and can then be used to mark all recursive methodExecution as such. Necessary to later compute
     * fixpoints for these methodExecution instead of descending infinitely.
     */
    private final TarjanAlgorithm recursiveMethodDetection = new TarjanAlgorithm();
    /**
     * Maps the Units from the Jimple control flow of the method which is
     * currently translated to program counters.
     */
    private Map<Unit, Integer> currentUnitToPC;
    /**
     * necessary to fill the call graph during translation
     */
    private Method currentMethod;

    /**
     * Default initialization for TopLevelTranslation.
     * Sets the firstLevel of the translation hierarchy to
     * {@link StandardAbstractSemantics}
     */
    public TopLevelTranslation(SceneObject sceneObject) {

        super(sceneObject);
        firstLevel = new StandardAbstractSemantics(this);
    }

    /**
     * Initializes the TopLevelTranslation with a custom first level.
     *
     * @param firstLevel The custom first level of the translation process.
     */
    public TopLevelTranslation(SceneObject sceneObject, JimpleToAbstractSemantics firstLevel) {

        super(sceneObject);
        this.firstLevel = firstLevel;
        firstLevel.setTopLevel(this);
    }

    /**
     * First fills the methodMapping with new abstractMethods for each method in
     * the main class. Then fills these methodExecution each with the corresponding
     * translation of statements.<br>
     * Assumes that soot.Scene already containsSubsumingState the Jimple code that should be
     * translated.
     *
     * @see #translateMethod(SootMethod)
     */
    public void translate() {

        ArrayList<SootMethod> methods = new ArrayList<>();

        //SootClass mainClass = Scene.v().getMainClass();

        // Determine all necessary (non-library) classes and its methodExecution
        Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
        for (SootClass sootClass : sootClasses) {
            methods.addAll(sootClass.getMethods());
        }

        for (SootMethod method : methods) {
            logger.trace("Found soot method: " + method.getSignature());

            String shortName = shortMethodSignature(method);

            String signature = method.getSignature();

            final Method abstractMethod = scene().getOrCreateMethod(signature);
            abstractMethod.setName(shortName);
            recursiveMethodDetection.addMethodAsVertex(abstractMethod);
        }
        for (SootMethod method : methods) {
            translateMethod(method);
        }

        recursiveMethodDetection.markRecursiveMethods();
    }

    /**
     * Computes a shortened version of a method name that does not include all packages as a prefix.
     *
     * @param method The method whose shortened signature should be determined.
     * @return A shortened method signature without package information.
     */
    private String shortMethodSignature(SootMethod method) {

        /*
        StringBuilder params = new StringBuilder("(");
        boolean isFirst = true;
        for (soot.Type type : method.getParameterTypes()) {
            if (!isFirst) {
                params.append(", ");
            }
            isFirst = false;
            params.append(getShortName(type));
        }
        params.append(")");
        return getShortName(method.getReturnType()) + " " + method.getName() + params;
        */
        return method.getName();
    }

    /**
     * Computes a shortened version of a type name that does not include all packages as a prefix.
     *
     * @param type The type whose shortened name should be determined.
     * @return The shortened name of the given type.
     */
    private String getShortName(soot.Type type) {

        // escaped name was recently removed in soot.Type
        //String[] splitted = type.getEscapedName().split("\\.");

        String[] splitted = type.toString().split("\\.");
        return splitted[splitted.length - 1];
    }

    /**
     * Sets {@link #currentUnitToPC} to a mapping with the units in this method.
     * Then translates each statement and sets the resulting list as control
     * flow in the corresponding abstractMethod.
     *
     * @param method The method to translate.
     */
    private void translateMethod(SootMethod method) {

        try {
			currentMethod = scene().getMethodIfPresent(method.getSignature());
		
        currentUnitToPC = new LinkedHashMap<>();

        Chain<Unit> units = method.getActiveBody().getUnits();
        Unit curr = units.getFirst();

        for (int i = 0; i < units.size(); i++) {
            currentUnitToPC.put(curr, i);
            curr = units.getSuccOf(curr);
        }

        List<SemanticsCommand> programStatements = new ArrayList<>();

        curr = units.getFirst();
        for (int i = 0; i < units.size(); i++) {
            programStatements.add(translateStatement((soot.jimple.Stmt) curr, i));
            curr = units.getSuccOf(curr);
        }

        logger.trace("method Name: " + method.getSignature());

        currentMethod.setBody(new ProgramImpl(programStatements));

        } catch (ElementNotPresentException e) {
			logger.error("The method " + method.getSignature() + " was not correctly instantiated");
		}
    }

    @Override
    public Statement translateStatement(Stmt input, int pc) {

        return firstLevel.translateStatement(input, pc);
    }

    @Override
    public Value translateValue(soot.Value input) {

        return firstLevel.translateValue(input);
    }

    @Override
    public Type translateType(soot.Type input) {

        return firstLevel.translateType(input);
    }

    /**
     * Gets the program counter associated with the requested unit in the method
     * which is currently translated.
     *
     * @param unit A unit in the jimple control flow.
     * @return The associated program counter.
     */
    int getPCforUnit(Unit unit) {

        return this.currentUnitToPC.get(unit);
    }

    /**
     * Gets the abstract method for the given signature. If the method is not
     * from the main class, it creates a default method with an empty body for
     * this signature and adds it to the methodMapping.
     *
     * @param signature The signature of the requested method.
     * @return The corresponding abstract method.
     */
    public Method getMethodForCall(String signature) {

        Method res = scene().getOrCreateMethod(signature);
        recursiveMethodDetection.addCallEdge(currentMethod, res);
        if (res.getBody() == null) {

            String displayName = shortMethodSignature(Scene.v().getMethod(signature));
            res.setName(displayName);

            List<SemanticsCommand> defaultControlFlow = new ArrayList<>();
            defaultControlFlow.add(new Skip(this, -1));
            res.setBody(new ProgramImpl(defaultControlFlow));

            logger.warn("Method " + signature + " replaced by empty default method.");

        }

        return res;
    }

    @Override
    public void setTopLevel(TopLevelTranslation topLevel) {
        // not necessary
    }
}
