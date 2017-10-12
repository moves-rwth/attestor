package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import de.rwth.i2.attestor.main.settings.Settings;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.stateSpaceGeneration.Semantics;
import de.rwth.i2.attestor.stateSpaceGeneration.StateSpaceGenerationAbortedException;
import de.rwth.i2.attestor.types.Type;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.util.Chain;

import java.util.*;

/**
 * This class organizes the actual translation process by splitting a Jimple program
 * into methods and methods into units. The lower translation layers can ask
 * this element for the pc of a given unit and the method-translation for a
 * given method-signature.
 * 
 * The translation process is started by translate().
 * 
 * This class does not specify actual translation rules for statements/values/types. Those
 * can be find in the translation hierarchy starting in {@link #firstLevel}
 * 
 * @author Hannah Arndt, Christoph
 *
 */
public class TopLevelTranslation implements JimpleToAbstractSemantics {

	/**
	 * The logger for this class.
	 */
	private static final Logger logger = LogManager.getLogger("TopLevelTranslation");

	/**
	 * Maps the Units from the Jimple control flow of the method which is
	 * currently translated to program counters.
	 */
	private Map<Unit, Integer> currentUnitToPC;

	/**
	 * Maps the signature of occurring methods to an instance of abstract method
	 * which is either the abstract semantic translation for this method or a
	 * default method for those methods we do not translate.
	 */
	private Map<String, AbstractMethod> methodMapping;

	/**
	 * The next level in the translation hierarchy. This level is the first one
	 * to actually translate statements/values/types.
	 */
	private final JimpleToAbstractSemantics firstLevel;

	/**
	 * Default initialization for TopLevelTranslation.
	 * Sets the firstLevel of the translation hierarchy to
	 * {@link StandardAbstractSemantics}
	 */
	public TopLevelTranslation() {
		firstLevel = new StandardAbstractSemantics(this);
	}

	/**
	 * Initializes the TopLevelTranslation with a custom first level.
	 * @param firstLevel The custom first level of the translation process.
	 */
	public TopLevelTranslation( JimpleToAbstractSemantics firstLevel ){
		this.firstLevel = firstLevel;
		firstLevel.setTopLevel(this);
	}

	/**
	 * First fills the methodMapping with new abstractMethods for each method in
	 * the main class. Then fills these methods each with the corresponding
	 * translation of statements.<br>
	 * Assumes that soot.Scene already contains the Jimple code that should be
	 * translated.
	 * 
	 * @see de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod#AbstractMethod(String, AbstractMethod.StateSpaceFactory)
	 *      AbstractMethod(String name, AbstractMethod.StateSpaceFactory factory()())
	 * @see de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.invoke.AbstractMethod#setControlFlow(List)
	 *      AbstractMethod.setControlFlow(List program)
	 * @see #translateMethod(SootMethod)
	 */
	public void translate() {

		ArrayList<SootMethod> methods = new ArrayList<SootMethod>();

		SootClass mainClass = Scene.v().getMainClass();

		// Determine all necessary (non-library) classes and its methods
		Chain<SootClass> sootClasses = Scene.v().getApplicationClasses();
		for( SootClass sootClass : sootClasses ){
			methods.addAll( sootClass.getMethods() );
		}
		methodMapping = new HashMap<>();
		

		for (SootMethod method : methods) {
			logger.trace("Found soot method: " + method.getSignature());
			String shortName = shortMethodSignature(method);
			String signature = method.getSignature();
			try {
				methodMapping.put(signature, new AbstractMethod(signature, shortName, getStateSpaceFactory()));
			} catch (StateSpaceGenerationAbortedException e) {
				logger.fatal("Unexpected exception");
				throw new IllegalStateException(e.getMessage());
			}
		}
		for (SootMethod method : methods) {
			translateMethod(method);
		}
	}

	/**
	 * Computes a shortened version of a method name that does not include all packages as a prefix.
	 *
	 * @param method The method whose shortened signature should be determined.
	 * @return A shortened method signature without package information.
	 */
	private String shortMethodSignature(SootMethod method) {
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
	}

	/**
	 * Computes a shortened version of a type name that does not include all packages as a prefix.
	 *
	 * @param type The type whose shortened name should be determined.
	 * @return The shortened name of the given type.
	 */
	private String getShortName(soot.Type type) {
		String[] splitted = type.getEscapedName().split("\\.");
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

		currentUnitToPC = new HashMap<>();

		Chain<Unit> units = method.getActiveBody().getUnits();
		Unit curr = units.getFirst();

		for (int i = 0; i < units.size(); i++) {
			currentUnitToPC.put(curr, i);
			curr = units.getSuccOf(curr);
		}

		List<Semantics> programStatements = new ArrayList<>();

		curr = units.getFirst();
		for (int i = 0; i < units.size(); i++) {
			programStatements.add(translateStatement((soot.jimple.Stmt) curr, i));
			curr = units.getSuccOf(curr);
		}

		logger.trace("method Name: " + method.getSignature());
		methodMapping.get(method.getSignature()).setControlFlow(programStatements);

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
	public AbstractMethod getMethod(String signature) throws StateSpaceGenerationAbortedException {
		AbstractMethod res = this.methodMapping.get(signature);
		if (res != null) {
			return res;
		} else {
			String displayName = shortMethodSignature( Scene.v().getMethod(signature) );
			AbstractMethod.StateSpaceFactory factory = getStateSpaceFactory();
			AbstractMethod defaultMethod = new AbstractMethod(signature + " (only default - empty Method)",
					displayName, factory);
			methodMapping.put(signature, defaultMethod);
			List<Semantics> defaultControlFlow = new ArrayList<>();
			defaultControlFlow.add(new Skip(-1));
			defaultMethod.setControlFlow(defaultControlFlow);

			logger.warn("Method " + signature + " replaced by empty default method.");

			return defaultMethod;
		}
	}

	private AbstractMethod.StateSpaceFactory getStateSpaceFactory() throws StateSpaceGenerationAbortedException {

		return (program, input, scopeDepth) -> {
			return Settings.getInstance()
					.factory()
					.createStateSpaceGenerator(
							program,
							input,
							scopeDepth
					).generate();

		};
	}

	@Override
	public void setTopLevel(TopLevelTranslation topLevel) {
		// not necessary		
	}
}
