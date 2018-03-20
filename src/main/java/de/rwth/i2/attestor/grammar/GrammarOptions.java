package de.rwth.i2.attestor.grammar;

/**
 *
 * Collects all options relevant to operations involving grammars such as abstraction and concretization.
 *
 * @author Christoph
 */
public interface GrammarOptions {

    default boolean isAdmissibleAbstractionEnabled() {
        return false;
    }

    default boolean isAdmissibleConstantsEnabled() {
        return true;
    }

    default boolean isAdmissibleMarkingsEnabled() {
        return true;
    }

    default boolean isAdmissibleFullEnabled() {
        return false;
    }

    default boolean isNoChainAbstractionEnabled() {
        return false;
    }

    default boolean isNoRuleCollapsingEnabled() {
        return false;
    }

    default boolean isIndexedModeEnabled() {
        return false;
    }

    default boolean isGarbageCollectionEnabled() {
        return true;
    }

}
