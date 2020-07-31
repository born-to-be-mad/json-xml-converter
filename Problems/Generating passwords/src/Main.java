import java.security.SecureRandom;
import java.util.Scanner;

public class Main {
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static  final SecureRandom random = new SecureRandom();

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int c = scanner.nextInt();
            int n = scanner.nextInt();

            System.out.println(generateRandomPassword(a, b, c, n));
        }
    }

    private static String generateRandomPassword(int length) {
        StringBuilder builder = new StringBuilder();
        int charsLength = CHARS.length();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(charsLength);
            builder.append(CHARS.charAt(randomIndex));
        }
        return builder.toString();
    }

    private static String generateRandomPassword(int upperCase, int lowerCase, int digits, int len) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < lowerCase; i++) {
            putRandomFromSource(builder, LOWERCASE);
        }
        for (int i = 0; i < upperCase; i++) {
            putRandomFromSource(builder, UPPERCASE);
        }
        for (int i = 0; i < digits; i++) {
            putRandomFromSource(builder, DIGITS);
        }
        for (int i = 0; i < len - (upperCase + lowerCase + digits); i++) {
            putRandomFromSource(builder, CHARS);
        }
        return builder.toString();
    }

    private static void putRandomFromSource(StringBuilder builder, String source) {
        char generated;
        do {
            generated = source.charAt(random.nextInt(source.length()));
        } while (builder.toString().indexOf(generated, builder.length() - 1) != -1);
        builder.append(generated);
    }
}
