public class List {

    private List next;

    public List(List next) {

        this.next = next;
    }

    public static void reverseList(List list) {

        List curr = list.next;
        list.setNext(null);

        while (curr != null) {
            List tmp = curr;
            curr = curr.next;
            tmp.setNext(list);
        }

    }

    public static void main(String[] args) {

        List list = new List(null);
        for (int i = 0; i < 10; i++) {
            list = new List(list);
        }

        //reverseList( list );
    }

    public void setNext(List next) {

        this.next = next;
    }
}