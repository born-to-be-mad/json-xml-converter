import java.util.*;

public class Main {
    private static final String UPPER_LETTERS = ".*[A-Z]+.*";
    private static final String LOWER_LETTERS = ".*[a-z]+.*";
    private static final String DIGITS = ".*[0-9]+.*";
    private static final String LENGTH = ".{12,}";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String password = scanner.nextLine();
        System.out.println(isPasswordSafe(password) ? "YES" : "NO");
    }

    private static boolean isPasswordSafe(String password) {
        return password.matches(UPPER_LETTERS) &&
               password.matches(LOWER_LETTERS) &&
               password.matches(DIGITS) &&
               password.matches(LENGTH);
    }
}
