/**
 * The classes in this package handle the translation of Jimple elements
 * such as Statements, Values and Types to semantics objects of our symbolic
 * execution.
 * <br><br>
 * The translation process starts in TopLevelTranslation which
 * does no translation itself but organises the translation.
 * <br><br>
 * The process is organised in layers. Each layer can hand down statements
 * it does not know, to lower layers. The lowest layer translates everything
 * to Skip/Undefined.
 * If the translation of some lement is defined in several layers, the topmost definition is the
 * relevant one.
 * <br><br>
 * <b>Where to start reading</b><br>
 * The top layer of the actual translation hierarchy is specified in TopLevelTranslation.
 * From there you can find the hierarchy by always following nextLayer. The translation result
 * for a certain statement/value/type will always be the topmost translation for it.
 * <br><br>
 * <b>How to modify the translation process:</b><br>
 * Create a new layer that translates all the statements where you
 * want to define a new semantic. Set the previous first layer as its successor.
 * By this you can also redefine semantics, since the translation starts
 * at the top-layer and will never use the "old" rules.
 *
 * @author Hannah Arndt, Christoph
 */
package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;