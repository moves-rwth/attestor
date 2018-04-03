package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import de.rwth.i2.attestor.main.scene.SceneObject;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.BranchingSkip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Skip;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.UndefinedValue;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.types.Type;
import de.rwth.i2.attestor.types.Types;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import soot.Unit;
import soot.UnitBox;

import java.util.List;


/**
 * Translates all Jimple objects into trivial program semantics.
 * That is each statement is translated into skip statements
 * and each value/type into undefined.
 * It should be the lowest layer of the translation hierarchy.
 *
 * @author Hannah Arndt, Christoph
 */
public class DefaultAbstractSemantics extends SceneObject implements JimpleToAbstractSemantics {

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
     *
     * @param topLevel The top level of the translation hierarchy.
     */
    DefaultAbstractSemantics(SceneObject sceneObject, TopLevelTranslation topLevel) {

        super(sceneObject);
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

        if (!input.fallsThrough()) {
            List<UnitBox> targets = input.getUnitBoxes();
            if (targets.size() >= 2) {
                Unit leftTarget = targets.get(0).getUnit();
                Unit rightTarget = targets.get(1).getUnit();
                res = new BranchingSkip(this, topLevel.getPCforUnit(leftTarget), topLevel.getPCforUnit(rightTarget));
            } else if (targets.size() == 1) {
                Unit target = targets.get(0).getUnit();
                res = new Skip(this, topLevel.getPCforUnit(target));
            } else {
                res = new Skip(this, -1);
            }
            if (targets.size() > 2) {
                logger.warn("Only the first two targets are considered");
            }

            return res;
        }
        logger.warn("Warning: " + input + " is not supported. Replaced by undefined.");

        res = new Skip(this, pc + 1);
        return res;
    }


    /**
     * Translates every value to {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.UndefinedValue}.
     */
    @Override
    public Value translateValue(soot.Value input) {

        logger.debug("Expression " + input + " is not supported. Replaced by undefined.");
        return new UndefinedValue();
    }

    /**
     * Translates every type to a new NodeTypeImpl instance with name "undefined Type".
     */
    @Override
    public Type translateType(soot.Type input) {

        return Types.UNDEFINED;
    }

    @Override
    public void setTopLevel(TopLevelTranslation topLevel) {

        this.topLevel = topLevel;
    }

}
