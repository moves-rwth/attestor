public class SLList {
    private SLList next;

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
