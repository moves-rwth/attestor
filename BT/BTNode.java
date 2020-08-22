public class BTNode {
    static int nondeterminism;
    private BTNode left;
    private BTNode right;

    static void robson(BTNode current, BTNode sentiel) {
        BTNode top = null;
        BTNode available = null;
        BTNode parent = sentiel;

        if (current == null) {
            return;
        }

        while (true) {
            // pre visit
            if (current.left != null) {
                BTNode old_left = current.left;
                current.left = parent;
                parent = current;
                current = old_left;
            } else if (current.right != null) {
                BTNode old_right = current.right;
                // in visit
                current.right = parent;
                parent = current;
                current = old_right;
            } else {
                boolean exchanged = false;
                available = current;
                // in visit
                while (!exchanged && parent != sentiel) {
                    BTNode old_cur = current;
                    if (parent.right == null) {
                        BTNode new_parent = parent.left;
                        // in visit
                        parent.left = current;
                        current = parent;
                        parent = new_parent;
                    } else if (parent.left == null) {
                        BTNode new_parent = parent.right;
                        parent.right = current;
                        current = parent;
                        parent = new_parent;
                    } else if (top != null && parent == top.right) {
                        BTNode old_top = top;
                        BTNode new_parent = parent.left;
                        parent.left = parent.right;
                        parent.right = current;
                        current = parent;
                        parent = new_parent;
                        top = top.left;
                        old_top.left = null;
                        old_top.right = null;
                    } else {
                        BTNode new_cur;
                        new_cur = parent.right;
                        available.left = top;
                        available.right = parent;
                        top = available;
                        available = null;
                        parent.right = current;
                        current = new_cur;
                        exchanged = true;
                    }
                    // post visit
                }
                if (!exchanged) {
                    // post visit
                    return;
                }
            }
        }
    }

    static void morris(BTNode root) {
        BTNode current, pre;
        current = root;

        while (current != null) {
            if (current.left == null) {
                // print
                current = current.right;
            } else {
                pre = current.left;
                while (pre.right != null && pre.right != current) {
                    pre = pre.right;
                }
                if (pre.right == null) {
                    pre.right = current;
                    current = current.left;
                } else {
                    pre.right = null;
                    // print
                    current = current.right;
                }
            }
        }
    }

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
