package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs;


public class BoolList {

    private final BoolList next;
    private boolean value;

    public BoolList(boolean value, BoolList next) {

        super();
        this.value = value;
        this.next = next;
    }

    public static void main(String[] args) {

        BoolList t1 = new BoolList(false, null);
        BoolList t2 = new BoolList(false, t1);
        BoolList t3 = new BoolList(false, t2);

        t3.invert();

    }

    private boolean hasNext() {

        return this.next != null;
    }

    public BoolList getNext() {

        return this.next;
    }

    public boolean getValue() {

        return this.value;
    }

    private void invert() {

        this.value = !this.value;
        if (this.hasNext()) {
            this.next.invert();
        }
    }
}
	
