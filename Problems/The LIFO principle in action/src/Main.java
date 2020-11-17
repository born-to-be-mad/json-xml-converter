import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        IntStream.range(0, scanner.nextInt())
                 .map(value -> scanner.nextInt())
                 .collect(ArrayList::new,
                          (list, e) -> list.add(0, e),
                          (list1, list2) -> list1.addAll(0, list2))
                 .forEach(System.out::println);

    }
}
