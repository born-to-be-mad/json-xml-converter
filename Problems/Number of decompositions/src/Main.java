import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Main {

    private static List<String> result = new ArrayList<>();

    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        partition(n, n, "");
        Collections.reverse(result);
        result.forEach(System.out::println);
    }

    public static void partition(int n, int max, String prefix) {
        if (n == 0) {
            result.add((prefix.trim()));
            return;
        }

        for (int i = Math.min(max, n); i >= 1; i--) {
            partition(n - i, i, prefix + " " + i);
        }
    }
}
