package de.rwth.i2.attestor.refinement.grammarRefinement;

import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.List;

/**
 * An iterator to traverse all possible assignments of objects to a fixed list of other objects.
 * For example, a state of a heap automaton may be assigned to every nonterminal edge of a heap configuration.
 *
 * @author Christoph
 */
class AssignmentIterator<E> {

    /**
     * The number of elements contained in a single assignment.
     */
    private final int size;

    /**
     * All possible elements that can be assigned to each element of an assignment.
     */
    private final List<List<E>> availableAssignments;

    /**
     * The number of possible elements that can be assigned to each element of an assignment.
     */
    private final TIntArrayList assignmentSizes;

    /**
     * The positions in the available assignments encoding the current assignment of the iterator.
     */
    private final TIntArrayList currentAssignment;

    /**
     * The latest position in the current assignment that determines the element that is changed first to obtain
     * the next assignment.
     */
    private int position;

    /**
     * Initializes the iterator.
     *
     * @param availableAssignments All possible elements that can be assigned to each element of an assignment.
     */
    AssignmentIterator(List<List<E>> availableAssignments) {

        this.size = availableAssignments.size();
        this.availableAssignments = availableAssignments;
        assignmentSizes = new TIntArrayList(size);
        currentAssignment = new TIntArrayList(size);
        for (int i = 0; i < size; i++) {
            currentAssignment.add(0);
            assignmentSizes.add(availableAssignments.get(i).size());
        }
        position = 0;

        for (List<E> list : availableAssignments) {
            if (list.isEmpty()) {
                position = size;
            }
        }
    }

    /**
     * @return True if and only if further elements can be obtained from the iterator.
     */
    public boolean hasNext() {

        return position < size;
    }

    /**
     * @return The next assignment provided that hasNext() returned true.
     */
    public List<E> next() {

        List<E> result = extractChoice();
        update();
        return result;

    }

    private void update() {

        if (!hasNext()) {
            return;
        }

        int inc = currentAssignment.get(position) + 1;
        int max = assignmentSizes.get(position);

        if (inc < max) {
            currentAssignment.set(position, inc);
            resetUpToPosition();
        } else {
            ++position;
            update();
        }


    }

    /**
     * @return The assignment encoded by currentAssignment.
     */
    private List<E> extractChoice() {

        List<E> result = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            result.add(availableAssignments.get(i).get(currentAssignment.get(i)));
        }
        return result;
    }

    /**
     * Resets trailing elements of the current assignment and sets the position back to the first element.
     */
    private void resetUpToPosition() {

        for (int i = 0; i < position; i++) {
            currentAssignment.set(i, 0);
        }
        position = 0;
    }
}
