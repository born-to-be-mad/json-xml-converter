import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        IntStream.range(0, scanner.nextInt() + 1)
                 .mapToObj(value -> scanner.nextLine())
                 .sorted()
                 .collect(Collectors.toCollection(LinkedHashSet::new))
                 .forEach(System.out::println);
    }
}
