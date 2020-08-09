public class BTNode {
    private BTNode left;
    private BTNode right;

    static BTNode lindstrom(BTNode root, BTNode sen) {
        if (root == null) return root;

        BTNode prev = sen;
        BTNode cur = root;

        while (cur != sen) {
            BTNode next = cur.left;

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
