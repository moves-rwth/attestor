package de.rwth.i2.attestor.semantics.jimpleSemantics.jimple.programs;

public class TestWithStatic {

    private static EasyList createList(int val, EasyList next) {

        EasyList res = new EasyList();
        res.next = next;
        res.value = val;

        return res;
    }

    private static boolean hasNext(EasyList list) {

        return list.next != null;
    }

    private static EasyList getNext(EasyList list) {

        return list.next;
    }


    public static void main(String[] args) {

        EasyList t1 = createList(0, null);
        EasyList t2 = createList(1, t1);

        EasyList curr = createList(2, t2);

        while (hasNext(curr)) {
            curr = getNext(curr);
        }

    }

}
