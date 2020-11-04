import java.util.ArrayList;
import java.util.List;

/**
 * Class to modify
 */
class ListMultiplicator {

    /**
     * Repeats original list content provided number of times
     *
     * @param list list to repeat
     * @param n    times to repeat, should be zero or greater
     */
    public static void multiply(List<?> list, int n) {
        if (n == 0) {
            list.clear();
        } else {
            multiplyCaptured(list, n);
        }
    }

    private static <T> void multiplyCaptured(List<T> list, int n) {
        List<T> tmp = new ArrayList<T>(list);
        while (--n > 0) {
            list.addAll(tmp);
        }
    }

    public static void main(String[] args) {
        ArrayList<Integer> integers = new ArrayList<>(List.of(1, 2, 3));
        multiply(integers, 0);
        System.out.println(integers);
    }
}
