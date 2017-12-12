package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs;


public class EasyList {

    public EasyList next;
    public int value;

    public EasyList() {
        //nothing - fields are set manually.
    }

    public static void main(String[] args) {

        EasyList t1 = new EasyList();

        t1.next = null;
        t1.value = 1;


        EasyList t2 = new EasyList();
        t2.next = t1;
        t2.value = 2;

        EasyList t3 = new EasyList();
        t3.next = t2;
        t3.value = 3;

        EasyList curr = t3;


        while (curr.next != null) {
            curr = curr.next;
        }
    }

}
