package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs;

class ReverseList {

    private ReverseList next;

    private ReverseList(ReverseList next) {

        this.next = next;
    }

    public static void main(String[] args) {

        ReverseList list1 = new ReverseList(null);


        ReverseList last = list1;
        //noinspection ConstantConditions
        while (last != null) {
            last = new ReverseList(last);
        }

        ReverseList reversedPart = null;
        ReverseList current = list1;
        while (current != null) {
            ReverseList next = current.next;
            current.next = reversedPart;
            reversedPart = current;
            current = next;
        }
    }
}
