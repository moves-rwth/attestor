public class SLList {
    static int nondeterminism;
    private SLList next;

    static SLList brent(SLList head) {
        if (head == null) {
            return null;
        }

        int power = 1;
        int length = 1;
        SLList first = head;
        SLList second = head.next;

        while (second != null && second != first) {
            if (length == power) {
                power *= 2;
                length = 0;
                first = second;
            }

            second = second.next;
            ++length;
        }

        if (second == null) {
            return null;
        }

        first = second = head;
        while (length > 0) {
            second = second.next;
            --length;
        }

        while (second != first) {
            second = second.next;
            first = first.next;
        }

        return first;
    }

    static boolean floyd(SLList head) {
        SLList slow = head;
        SLList fast = head;

        while (slow != null && fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;
            }
        }
        return false;
    }

    static void insertion(SLList head) {
        SLList x = head;
        SLList y = x;
        SLList sorted = null;
        SLList pred;
        SLList z;

        while (x != null) {
            y = x;
            x = x.next;
            pred = null;
            z = sorted;

            for (int i = 0; z != null && i < nondeterminism; i++) {
                pred = z;
                z = z.next;
            }

            y.next = z;
            if (pred != null) {
                pred.next = y;
            } else {
                sorted = y;
            }
        }

        while (sorted != null) {
            x = sorted;
            sorted = sorted.next;
            x = null;
        }
    }

    static void bubble(SLList head) {
        SLList x = head;
        SLList y = x;
        SLList pred;
        SLList succ;

        boolean sorted = false;
        while (!sorted) {
            sorted = true;
            y = x;
            pred = null;
            while (y != null && y.next != null) {
                if (nondeterminism == 42) {
                    succ = y.next;
                    if (pred != null) {
                        pred.next = succ;
                    } else {
                        x = succ;
                    }
                    y.next = succ.next;
                    succ.next = y;
                    sorted = false;
                }
                pred = y;
                y = y.next;
            }
        }

        while (x != null) {
            y = x;
            x = x.next;
            y = null;
        }
    }

    static void delete(SLList head) {
        SLList x = head;
        SLList y = x;
        SLList z = null;

        while (x != null) {
            if (nondeterminism < 42) {
                if (z != null) {
                    z.next = x.next;
                } else {
                    y = y.next;
                }
                x = null;
                break;
            }
            z = x;
            x = x.next;
        }
    }

    static void traverse(SLList head) {
        SLList cur = head;

        while (cur.next != null) {
            cur = cur.next;
        }
    }

    static SLList reverse(SLList head) {
        SLList reversedPart = null;
        SLList current = head;

        while (current != null) {
            SLList next = current.next;
            current.next = reversedPart;
            reversedPart = current;
            current = next;
        }

        return reversedPart;
    }

    public static void main(String[] args) {

    }
}
