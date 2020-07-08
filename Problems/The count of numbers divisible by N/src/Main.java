import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int n = scanner.nextInt();

            System.out.println(IntStream.rangeClosed(a, b)
                                        .filter(value -> value % n == 0)
                                        .count());
        }
    }
}
