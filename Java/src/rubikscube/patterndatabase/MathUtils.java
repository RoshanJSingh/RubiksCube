package rubikscube.patterndatabase;

public class MathUtils {

    public static int factorial(int n) {
        return n <= 1 ? 1 : n * factorial(n - 1);
    }

    public static int pick(int n, int k) {
        return factorial(n) / factorial(n - k);
    }

    public static int choose(int n, int k) {
        return (n < k) ? 0 : factorial(n) / (factorial(n - k) * factorial(k));
    }
}
