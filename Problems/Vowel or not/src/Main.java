import java.util.Scanner;

public class Main {
    private static final String VOWELS = "AaEeOoUuIi";

    public static boolean isVowel(char ch) {
        return VOWELS.contains("" + ch);
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        char letter = scanner.nextLine().charAt(0);
        System.out.println(isVowel(letter) ? "YES" : "NO");
    }
}
