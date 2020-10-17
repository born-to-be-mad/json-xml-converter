import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final List<String> seq = Arrays.stream(scanner.nextLine().split("\\s+"))
                                       .collect(Collectors.toList());
        final String symbol = scanner.nextLine();
        System.out.println(count(seq, symbol));
    }

    public static int count(List<String> sequence, String symbol) {
        return Collections.frequency(sequence, symbol);
    }
}
