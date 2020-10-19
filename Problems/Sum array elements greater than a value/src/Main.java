import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.LongStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String sizeOfArray = scanner.nextLine();
        LongStream longStream = Arrays.stream(scanner.nextLine().split("\\s+"))
                                      .mapToLong(Long::parseLong);
        int number = Integer.parseInt(scanner.nextLine());

        System.out.println(longStream.filter(value -> value > number).sum());
    }
}
