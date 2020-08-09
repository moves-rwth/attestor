public class BTree {
    private BTree left;
    private BTree right;

    static void path(BTree in, BTree out) {
        while (in.right != null && in.left != null) {
            BTree t = in;
            if (in.left == null){
                in = in.left;
                t.left = t.right;
                t.right = out;
            }
            else {
                in = in.right;
                t.right = out;
            }
            out = t;
        }
    }

    static BTree lindstrom(BTree root) {
        if (root == null) return root;
        BTree sen = new BTree();
        BTree prev = sen;
        BTree cur = root;

        while (cur != sen) {
            BTree next = cur.left;

            cur.left = cur.right;
            cur.right = prev;

            prev = cur;
            cur = next;

            if (cur == null) {
                cur = prev;
                prev = null;
            }
        }

        return cur;
    }


    public static void main(String[] args) {

    }
}
