import java.util.Scanner;

import static java.util.function.Predicate.not;

class Main {
    public static void main(String[] args) {
        new Scanner(System.in)
            .tokens()
            .takeWhile(not("0"::equals))
            .mapToInt(Integer::parseInt)
            .max()
            .ifPresent(System.out::print);
    }
}
