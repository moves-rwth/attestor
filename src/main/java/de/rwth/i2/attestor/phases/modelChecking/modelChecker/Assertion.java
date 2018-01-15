package de.rwth.i2.attestor.phases.modelChecking.modelChecker;

import de.rwth.i2.attestor.LTLFormula;
import de.rwth.i2.attestor.generated.node.ANextLtlform;
import de.rwth.i2.attestor.generated.node.Node;

import java.util.LinkedList;

/**
 * This class implements the states of the tableau method proof structure. Each state consists
 * of a program state and a list of (sub)formulae, which together form an assertion, that has
 * to be discharged.
 *
 * @author christina
 */

public class Assertion {

    final int progState;
    final LinkedList<Node> formulae;
    // The previous assertion or null if the assertion is the root
    final Assertion parent;
    // specifies, if the assertions is known to hold
    boolean isTrue;
    /**
     * The state stored in this assertion belongs to a potential failure trace.
     * This is the case if the assertion
     * a) is the root of a proof structure
     * b) is a leaf of a proof structure
     * c) is the successor of a next transition
     */
    boolean isContainedInTrace;

    public Assertion(int progState, Assertion parent) {

        this.progState = progState;
        this.formulae = new LinkedList<>();
        isTrue = false;
        this.isContainedInTrace = false;
        this.parent = parent;
    }

    public Assertion(int progState, Assertion parent, LTLFormula formula) {

        this(progState, parent);
        // Note: To make sure that only "rule-nodes" are added, walk a step into the AST!
        this.formulae.add(formula.getASTRoot().getPLtlform());

    }

    /**
     * This constructor returns a new assertion as a copy of the provided one.
     * Note that the new assertion receives a shallow copy of the formulae list.
     *
     * @param assertion, the assertion to be copied
     */
    public Assertion(Assertion assertion) {

        this.progState = assertion.getProgramState();
        this.formulae = new LinkedList<>(assertion.getFormulae());
        this.isTrue = assertion.isTrue();
        this.isContainedInTrace = assertion.isContainedInTrace;
        this.parent = assertion.parent;
    }

    /**
     * This constructor returns a new assertion as a copy of the provided one.
     * Note that the new assertion receives a shallow copy of the formulae list.
     */
    public Assertion(int progState, Assertion parent, boolean isContainedInTrace) {

        this(progState, parent);
        this.isContainedInTrace = true;
    }

    LinkedList<Node> getFormulae() {

        return this.formulae;
    }

    public int getProgramState() {

        return this.progState;
    }

    public boolean isTrue() {

        return this.isTrue;
    }

    public Node getFirstFormula() {

        return this.formulae.getFirst();
    }

    public void removeFormula(Node formula) {

        this.formulae.remove(formula);
    }

    /**
     * This method adds a formula to the list of formulae in case it is not already contained
     * (otherwise no action is performed).
     * Note that add respects the insertion order forced by the tableau method, i.e. next formulae
     * are inserted at the back of the list, while all remaining formulae are inserted at its
     * beginning.
     *
     * @param formula, the formula to be added
     */
    public void addFormula(Node formula) {

        if (!this.formulae.contains(formula)) {
            if (formula instanceof ANextLtlform) {
                this.formulae.addLast(formula);
            } else {
                this.formulae.addFirst(formula);
            }
        }
    }

    public void setTrue() {

        this.isTrue = true;
    }

    public void removeFirstFormula(Node node) {

        this.formulae.removeFirst();

    }

    public String stateIDAndFormulaeToString() {

        return this.progState + this.formulae.toString();


    }

    public Assertion getParent() {

        return parent;
    }

}
