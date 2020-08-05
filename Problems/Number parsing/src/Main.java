import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String regex = "[+-]?(0|[1-9]\\d*)([.,](0|\\d*[1-9]))?";
            System.out.println(scanner.nextLine().matches(regex) ? "YES" : "NO");
        }
    }
}
