public class DLList {
    static int nondeterminism;
    public DLList next;
    public DLList prev;

    static void insert(DLList head, DLList ins) {
        DLList x = head;
        DLList y = x;

        while (y != null) {
            if (nondeterminism < 42) {
                DLList z = ins;
                z.next = y.next;
                z.prev = y;
                y.next = z;
                if (z.next != null) {
                    z.next.prev = z
                }
                break;
            }
            y = y.next;
        }

        while (x != null) {
            y = x;
            x = x.next;
            y = null;
        }
    }

    static DLList last(DLList head) {
        DLList pos = head;

        while (pos.next != null) {
            pos = pos.next;
        }

        return pos;
    }

    static DLList first(DLList head) {
        DLList pos = head;

        while (pos.prev != null) {
            pos = pos.prev;
        }

        return pos;
    }

    static void traverse(DLList head) {
        DLList cur = head;

        while (cur != null) {
            cur = cur.next;
        }
    }

    static void reverse(DLList head) {
        DLList pos = head;

        while (pos != null) {
            DLList tmp = pos.next;

            pos.next = pos.prev;
            pos.prev = tmp;

            pos = pos.prev;
        }

        head = pos;
    }

    public static void main(String[] args) {

    }
}