public class BTree {
    private BTree left;
    private BTree right;

    public void findPath(BTree in, BTree out) {
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

    public static void main(String[] args) {

    }
}
