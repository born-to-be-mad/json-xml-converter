import java.util.Scanner;

public class Main {
    private static final  String ZERO_TO_255 = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final  String IP_REGEXP =
        ZERO_TO_255 + "\\." + ZERO_TO_255 + "\\." + ZERO_TO_255 + "\\." + ZERO_TO_255;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            boolean result = scanner.nextLine().matches(IP_REGEXP);
            System.out.println(result ? "YES" : "NO");
        }
    }
}
