package de.rwth.i2.attestor.automata;

import gnu.trove.list.array.TIntArrayList;

/**
 * Created by cmath on 7/19/17.
 */
public class ChoicesIterator {

    private int size;
    private TIntArrayList possibleChoices;
    private TIntArrayList currentChoice;
    private int position;

    public ChoicesIterator(TIntArrayList possibleChoices) {

        assert(possibleChoices != null);

        this.size = possibleChoices.size();
        this.possibleChoices = possibleChoices;
        currentChoice = new TIntArrayList();
        for(int i=0; i < size; i++) {
            currentChoice.add(0);
        }
        position = 0;
    }

    public boolean hasNext() {

        return position < size;
    }

    public TIntArrayList next() {

        TIntArrayList result = new TIntArrayList(currentChoice);

        int inc = currentChoice.get(position) + 1;
        int max = possibleChoices.get(position);
        if(inc < max) {
            resetUpToPosition();
        } else {
            ++position;
            if(hasNext()) {
                resetUpToPosition();
            }
        }

        return result;
    }

    private void resetUpToPosition() {
        for(int i=0; i < position; i++) {
            currentChoice.set(i, 0);
        }
        position = 0;
    }
}
