package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.*;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.UndefinedValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.TypeFactory;
import de.rwth.i2.attestor.util.DebugMode;
import soot.Unit;
import soot.UnitBox;



/**
 * Translates all Jimple objects into trivial program semantics.
 * That is each statement is translated into skip statements
 * and each value/type into undefined.
 * It should be the lowest layer of the translation hierarchy.
 * 
 * @author Hannah Arndt, Christoph
 *
 */
public class DefaultAbstractSemantics implements JimpleToAbstractSemantics {

    /**
     * The logger of this class.
     */
	private static final Logger logger = LogManager.getLogger("bytecodeSemantics.translation.DefaultAbstractSemantics");

    /**
     * The top level of the translation.
     */
	private TopLevelTranslation topLevel;

    /**
     * Initializes this translation level.
     * @param topLevel The top level of the translation hierarchy.
     */
	DefaultAbstractSemantics(TopLevelTranslation topLevel ) {
		this.topLevel = topLevel;
	}

	/**
	 * Translates all input statements to {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip}
	 * or {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.BranchingSkip}
	 * if the input statement has multiple successors. In any case we consider no more than two successors.
	 */
	@Override
	public Statement translateStatement(soot.jimple.Stmt input, int pc) {
		Statement res;
		
		if( ! input.fallsThrough() ){
			List<UnitBox> targets = input.getUnitBoxes();
			if( targets.size() >= 2){
				Unit leftTarget = targets.get(0).getUnit();
				Unit rightTarget = targets.get(1).getUnit();
				res = new BranchingSkip( topLevel.getPCforUnit(leftTarget), topLevel.getPCforUnit(rightTarget));
			}else if(targets.size() == 1 ){
				Unit target = targets.get(0).getUnit();
				res = new Skip(topLevel.getPCforUnit(target) ); 
			}else{
				res = new Skip(-1);
			}
			if( DebugMode.ENABLED && targets.size() > 2 ){
				logger.warn("Only the first two targets are considered");
			}

			return res;
		}
		if(DebugMode.ENABLED ) {
			   logger.warn("Warning: " + input + " is not supported. Replaced by undefined.");
		}
		
		res = new Skip( pc+1);
		return res;
	}


	/**
	 * Translates every value to {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.UndefinedValue}.
	 */
	@Override
	public Value translateValue(soot.Value input) {
	
		if(DebugMode.ENABLED) {
			   logger.warn("Warning: " + input + " is not supported. Replaced by undefined.");
		}
		
		return new UndefinedValue();
	}

	/**
	 * Translates every type to a new NodeTypeImpl instance with name "undefined Type".
	 */
	@Override
	public Type translateType(soot.Type input) {
		
		return TypeFactory.getInstance().getType("undefined Type");
	}

	@Override
	public void setTopLevel(TopLevelTranslation topLevel) {
		this.topLevel = topLevel;
	}

}
