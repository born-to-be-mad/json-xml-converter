import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String result = Arrays.stream(input.split("")).map(s -> s.repeat(2)).collect(Collectors.joining());
        System.out.println(result);
    }
}
