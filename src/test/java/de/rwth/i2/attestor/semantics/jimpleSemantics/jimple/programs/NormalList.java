package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs;

public class NormalList {

    private final NormalList next;
    private final int value;

    public NormalList(NormalList next, int value) {

        this.next = next;
        this.value = value;
    }

    public static void main(String[] args) {

        NormalList t1 = new NormalList(null, 1);
        NormalList t2 = new NormalList(t1, 2);

        NormalList curr = new NormalList(t2, 3);
        while (curr.hasNext()) {
            curr = curr.getNext();
        }
    }

    public int getValue() {

        return value;
    }

    private boolean hasNext() {

        return this.next != null;
    }

    private NormalList getNext() {

        return this.next;
    }

}
