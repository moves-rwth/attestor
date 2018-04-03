package de.rwth.i2.attestor.main.scene;

/**
 * Collects all options that customize the state space generation.
 *
 * @author Hannah Arndt, Christoph
 */
public class Options {

    protected Options() {}

    /**
     * Enabling this option results in dead variables (variables that are not accessed before being rewritten in the
     * following) being deleted in order to enable more possible abstractions.
     */
    private boolean removeDeadVariables = true;
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
    private boolean postProcessingEnabled = false;
    /**
     * If enabled, external nodes of rules are collapsed before applying abstraction
     */
    private boolean ruleCollapsingEnabled = true;

    // -----------------------------------------------------------------------------------

    private boolean admissibleAbstractionEnabled = false;

    private boolean admissibleConstantsEnabled = false;

    private boolean admissibleMarkingsEnabled = false;

    private boolean admissibleFullEnabled = false;

    private boolean chainAbstractionEnabled = true;

    /**
     * Enabling this option leads to using a program analysis based on indexed hyperedge replacement grammars.
     */
    private boolean indexedModeEnabled = false;

    private boolean canonicalEnabled = false;

    private int maxStateSpace = 5000;

    private int maxHeap = 50;

    // -----------------------------------------------------------------------------------

    public void setPostProcessingEnabled(boolean enabled) {

        this.postProcessingEnabled = enabled;
    }

    public void setRemoveDeadVariables(boolean removeDeadVariables) {

        this.removeDeadVariables = removeDeadVariables;
    }

    public void setGrammarRefinementEnabled(boolean enabled) {

        grammarRefinementEnabled = enabled;
    }

    public void setGarbageCollectionEnabled(boolean enabled) {

        this.garbageCollectionEnabled = enabled;
    }

    public void setAdmissibleAbstractionEnabled(boolean admissibleAbstractionEnabled) {
        this.admissibleAbstractionEnabled = admissibleAbstractionEnabled;
    }

    public void setAdmissibleConstantsEnabled(boolean admissibleConstantsEnabled) {
        this.admissibleConstantsEnabled = admissibleConstantsEnabled;
    }

    public void setAdmissibleMarkingsEnabled(boolean admissibleMarkingsEnabled) {
        this.admissibleMarkingsEnabled = admissibleMarkingsEnabled;
    }


    public void setAdmissibleFullEnabled(boolean admissibleFullEnabled) {
        this.admissibleFullEnabled = admissibleFullEnabled;
    }

    public void setChainAbstractionEnabled(boolean chainAbstractionEnabled) {
        this.chainAbstractionEnabled = chainAbstractionEnabled;
    }

    public void setRuleCollapsingEnabled(boolean ruleCollapsingEnabled) {
        this.ruleCollapsingEnabled = ruleCollapsingEnabled;
    }

    public void setIndexedModeEnabled(boolean indexedModeEnabled) {
        this.indexedModeEnabled = indexedModeEnabled;
    }


    public void setCanonicalEnabled(boolean canonicalEnabled) {
        this.canonicalEnabled = canonicalEnabled;
    }


    public void setMaxStateSpace(int maxStateSpace) {
        this.maxStateSpace = maxStateSpace;
    }


    public void setMaxHeap(int maxHeap) {
        this.maxHeap = maxHeap;
    }


    public int getMaxStateSpace() {
        return maxStateSpace;
    }

    public int getMaxHeap() {
        return maxHeap;
    }

    public boolean isRemoveDeadVariables() {

        return removeDeadVariables;
    }

    public boolean isPostprocessingEnabled() {

        return postProcessingEnabled && !canonicalEnabled;
    }

    public boolean isIndexedMode() {

        return indexedModeEnabled;
    }

    public boolean isGrammarRefinementEnabled() {

        return grammarRefinementEnabled;
    }

    public boolean isCanonicalEnabled() {
        return canonicalEnabled;
    }

    public boolean isRuleCollapsingEnabled() {

        return ruleCollapsingEnabled;
    }

    public boolean isGarbageCollectionEnabled() {
        return garbageCollectionEnabled;
    }

    public boolean isChainAbstractionEnabled() {
        return chainAbstractionEnabled && !canonicalEnabled;
    }

    public boolean isAdmissibleMarkingsEnabled() {
        return admissibleMarkingsEnabled || canonicalEnabled;
    }

    public boolean isAdmissibleConstantsEnabled() {
        return admissibleConstantsEnabled || canonicalEnabled;
    }

    public boolean isAdmissibleFullEnabled() {
        return admissibleFullEnabled || canonicalEnabled;
    }

    public boolean isAdmissibleAbstractionEnabled() {
        return admissibleAbstractionEnabled;
    }
}
