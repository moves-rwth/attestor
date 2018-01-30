package de.rwth.i2.attestor.main.scene;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Collects all options that customize the state space generation.
 *
 * @author Hannah Arndt, Christoph
 */
public class Options {

    /**
     * The logger of this class.
     */
    private static final Logger logger = LogManager.getLogger("Options");
    /**
     * The set of variables that will never ever be eliminated.
     */
    private final Set<String> keptVariables = new LinkedHashSet<>();
    private final Set<String> usedSelectorLabels = new LinkedHashSet<>();
    private final Set<String> grammarSelectorLabels = new LinkedHashSet<>();
    /**
     * The maximal number of states before state space generation is given up.
     */
    private int maxStateSpaceSize = 3000;
    /**
     * The maximal number of nodes in a single heap configuration before state space generation is given up.
     */
    private int maxStateSize = 50;
    /**
     * The minimal distance between variables in a heap configuration and embeddings used for abstraction.
     * Increasing this number allows to use a less aggressive abstraction.
     */
    private int abstractionDistance = 0;
    /**
     * Indicates whether the abstraction distance for the null node is set to 0 (otherwise or the specified distance is chosen).
     */
    private boolean aggressiveNullAbstraction = true;
    /**
     * Enabling this option results in dead variables (variables that are not accessed before being rewritten in the
     * following) being deleted in order to enable more possible abstractions.
     */
    private boolean removeDeadVariables = true;
    /**
     * Enabling this option leads to using a program analysis based on indexed hyperedge replacement grammars.
     */
    private boolean indexedMode = false;
    /**
     * Enabled this option leads to using RefinedNonterminals in graph grammars.
     */
    private boolean grammarRefinementEnabled = false;
    /**
     * If true, unreachable parts of heap are regularly eliminated.
     */
    private boolean garbageCollectionEnabled = true;
    /**
     * Determines if post-processing is applied to generated state spaces.
     */
    private boolean postProcessingEnabled = true;
    /**
     * If enabled, external nodes of rules are collapsed before applying abstraction
     */
    private boolean ruleCollapsingEnabled = true;

    protected Options() {}

    public void addKeptVariable(String variableName) {

        keptVariables.add(variableName);
    }

    public boolean isKeptVariableName(String variableName) {

        return keptVariables.contains(variableName);
    }

    /**
     * @return True if post-processing is applied to generated state spaces.
     */
    public boolean isPostprocessingEnabled() {

        return postProcessingEnabled;
    }

    /**
     * @param
     */
    public void setPostProcessingEnabled(boolean enabled) {

        this.postProcessingEnabled = enabled;
    }

    /**
     * @return The maximal size of state spaces before state space generation is given up.
     */
    public int getMaxStateSpaceSize() {

        return maxStateSpaceSize;
    }

    /**
     * @param maxStateSpaceSize The maximal size of state spaces before state space generation is given up.
     */
    public void setMaxStateSpaceSize(int maxStateSpaceSize) {

        this.maxStateSpaceSize = maxStateSpaceSize;
    }

    /**
     * @return The maximal size of heap configurations before state space generation is given up.
     */
    public int getMaxStateSize() {

        return maxStateSize;
    }

    /**
     * @param maxStateSize The maximal size of heap configurations before state space generation is given up.
     */
    public void setMaxStateSize(int maxStateSize) {

        this.maxStateSize = maxStateSize;
    }

    /**
     * @return The minimal distance between variables and nodes in an embedding before abstraction is performed.
     */
    public int getAbstractionDistance() {

        return abstractionDistance;
    }

    /**
     * @param abstractionDistance The minimal distance between variables and nodes in an
     *                            embedding before abstraction is performed.
     */
    public void setAbstractionDistance(int abstractionDistance) {

        if (abstractionDistance != 0 && abstractionDistance != 1) {
            throw new IllegalArgumentException("Abstraction distance must be either '0' or '1'.");
        }
        this.abstractionDistance = abstractionDistance;
    }

    /**
     * @return True if and only if the set abstraction distance should be ignored for the null node (and instead set to 0).
     */
    public boolean getAggressiveNullAbstraction() {

        return aggressiveNullAbstraction;
    }

    /**
     * @param aggressiveNullAbstraction True if and only if the abstraction distance
     *                                  should be ignored for the null node.
     */
    public void setAggressiveNullAbstraction(boolean aggressiveNullAbstraction) {

        if (abstractionDistance == 0 && aggressiveNullAbstraction) {
            logger.warn("The option 'aggressiveNullAbstraction' will have no effect " +
                    "since the dereference depth is already set to 0");
        }
        this.aggressiveNullAbstraction = aggressiveNullAbstraction;
    }

    /**
     * @return True if and only if dead variables are deleted from heap configurations whenever possible.
     */
    public boolean isRemoveDeadVariables() {

        return removeDeadVariables;
    }

    /**
     * @param removeDeadVariables True if and only if dead variables are deleted from
     *                            heap configurations whenever possible.
     */
    public void setRemoveDeadVariables(boolean removeDeadVariables) {

        this.removeDeadVariables = removeDeadVariables;
    }

    /**
     * @return True if and only if an indexed program analysis is performed.
     */
    public boolean isIndexedMode() {

        return indexedMode;
    }

    /**
     * @param indexedMode True if and only if an indexed program analysis is performed.
     */
    public void setIndexedMode(boolean indexedMode) {

        this.indexedMode = indexedMode;
    }

    public boolean isGrammarRefinementEnabled() {

        return grammarRefinementEnabled;
    }

    public void setGrammarRefinementEnabled(boolean enabled) {

        grammarRefinementEnabled = enabled;
    }

    /**
     * @return True if and only if the symbolic execution has to use garbage collection
     */
    public boolean isGarbageCollectionEnabled() {

        return garbageCollectionEnabled;
    }

    /**
     * @param enabled True if and only if the symbolic execution should perform garbage collection
     */
    public void setGarbageCollectionEnabled(boolean enabled) {

        this.garbageCollectionEnabled = enabled;
    }

    public void addGrammarSelectorLabel(String selector) {

        grammarSelectorLabels.add(selector);
    }

    public void addUsedSelectorLabel(String selector) {

        usedSelectorLabels.add(selector);
    }

    public Set<String> getGrammarSelectorLabels() {

        return grammarSelectorLabels;
    }

    public Set<String> getUsedSelectorLabels() {

        return usedSelectorLabels;
    }

    public boolean isRuleCollapsingEnabled() {

        return ruleCollapsingEnabled;
    }

    public void setRuleCollapsingEnabled(boolean enabled) {

        ruleCollapsingEnabled = enabled;
    }
}
