package de.rwth.i2.attestor.util;

import java.util.Iterator;

public class BitSequence implements Iterable<BitSequence> {

    private final int length;
    private int current;

    public BitSequence(int length) {
        this.length = length;
        this.current = -1; // adhere to iterator scheme that next has to be called first.
    }

    public boolean isSet(int bit) {
        return (current & (1 << bit)) > 0;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {

       return Integer.toBinaryString(current);
    }

    @Override
    public Iterator<BitSequence> iterator() {

        return new Iterator<BitSequence>() {
            @Override
            public boolean hasNext() {

                return (current+1) < (1 << length);
            }

            @Override
            public BitSequence next() {
                ++current;
                return BitSequence.this;
            }
        };
    }
}
