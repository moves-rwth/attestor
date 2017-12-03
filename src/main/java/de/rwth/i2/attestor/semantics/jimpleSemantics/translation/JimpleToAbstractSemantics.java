package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement;
import de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value;
import de.rwth.i2.attestor.types.Type;

/*
 * The general interface to translate Jimple objects into our own representation of programs:
 * <ul>
 *     <li>{@link soot.jimple.Stmt} is translated into {@link Statement}.</li>
 *     <li>{@link soot.jimple.Value} is translated into {@link Value}.</li>
 *     <li>{@link soot.jimple.Type} is translated into {@link Type}.</li>
 * </ul>
 *
 * @author Hannah Arndt, Christoph
 *
 */
public interface JimpleToAbstractSemantics {

    /**
     * Determines the highest level in the translation hierarchy.
     * This is the first level of the translation that is called during the translation.
     *
     * @param topLevel The first level that is invoked during the translation.
     */
    void setTopLevel(TopLevelTranslation topLevel);

    /**
     * Translates Jimple statements to instances of
     * {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.statements.Statement}.
     *
     * @param input The Jimple statement to translate.
     * @param pc    The program counter associated to this statement.
     * @return The translated statement.
     */
    Statement translateStatement(soot.jimple.Stmt input, int pc);

    /**
     * Translates Jimple values to instances of
     * {@link de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.values.Value}.
     *
     * @param input the Jimple value to translate.
     * @return The translated value.
     */
    Value translateValue(soot.Value input);

    /**
     * Translates Jimple types into {@link Type}.
     *
     * @param input The type to translate.
     * @return The translated type.
     */
    Type translateType(soot.Type input);
}
