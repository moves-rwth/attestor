public class BTNode {
    private BTNode left;
    private BTNode right;

    static void traverse(BTNode tree) {
        if (tree != null) {
            traverse(tree.left);
            traverse(tree.right);
        }
    }

    static BTNode left(BTNode tree) {
        while (tree.left != null) {
            tree = tree.left;
        }

        return tree;
    }

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
