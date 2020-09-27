import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int size = scanner.nextInt();
        int[] array = new int[size + 1];
        IntStream.rangeClosed(1, size).forEach(i -> array[i] = scanner.nextInt());
        array[0] = array[size];

        IntStream.range(0, size).forEach(i -> System.out.printf("%s ", array[i]));
    }
}
