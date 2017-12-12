public class RecursionDetectionInput {

    public static int noCalls(int y) {

        int x = 4;
        return x + y;
    }

    public static int nonRecursiveCaller() {

        boolean b = true;
        if (nonRecursiveCallee(b)) {
            return 2;
        }
        return 5;
    }

    private static boolean nonRecursiveCallee(boolean b) {

        return !b;
    }

    public static int selfLoop(int i) {

        if (i < 2) {
            return 1;
        } else {
            return selfLoop(i - 1) + selfLoop(i - 2);
        }
    }

    public static int indirectRecursion1() {

        return indirectRecursion2(5);
    }

    private static int indirectRecursion2(int i) {

        return indirectRecursion1();
    }

    public static int callSeveral() {

        return selfLoop(8) + indirectRecursion1() + noCalls(3);
    }

    public static void main(String[] args) {

        callSeveral();
    }
}