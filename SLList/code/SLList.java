public class SLList {

    private SLList next;

    public SLList(SLList next) {
        this.next = next;
    }

    public void reverse(SLList head) {
        SLList reversedPart = null;
        SLList current = head;

        while (current != null) {
            SLList next = current.next;
            current.next = reversedPart;
            reversedPart = current;
            current = next;
        }
    }

    public static void main(String[] args) {

    }
}
