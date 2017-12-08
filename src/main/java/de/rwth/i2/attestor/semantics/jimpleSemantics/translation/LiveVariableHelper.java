package de.rwth.i2.attestor.semantics.jimpleSemantics.translation;

import soot.tagkit.Tag;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * An auxiliary class that determines the set of all variables names that are dead (not accessed afterwards before
 * being rewritten) for a given statement.
 * <p>
 * This information is extracted from Soot's live variable tagger.
 * Since dead variables are useless, but may prevent abstraction, the set of dead variables at each progrem location
 * is required such that they can be safely removed prior to canonicalization.
 *
 * @author Christoph
 */
class LiveVariableHelper {

    /**
     * Determines the set of names of dead variables for the given program location.
     *
     * @param input A statement at a specific program location whose dead variables should be determined.
     * @return A (safe) set of dead variables for the given program location.
     */
    static Set<String> extractLiveVariables(soot.jimple.Stmt input) {

        Set<String> liveVariables = new LinkedHashSet<>();
        for (Tag t : input.getTags()) {

            String tagString = t.toString();
            if (tagString.contains("Live Variable: ")) {

                String varName = tagString.split("Live Variable: ")[1];
                liveVariables.add(varName);
            }
        }

        return liveVariables;
    }

}
