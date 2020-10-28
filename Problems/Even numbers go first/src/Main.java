import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int amount = scanner.nextInt();
        Deque<Integer> deque = new ArrayDeque<>();
        IntStream.range(0, amount)
                 .map(i -> scanner.nextInt())
                 .forEach(input -> {
                     if (input % 2 == 0) {
                         deque.addFirst(input);
                     } else {
                         deque.addLast(input);
                     }
                 });
        deque.forEach(System.out::println);
    }
}
