import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static final String PATTERN = "\\bprogram[a-z]*\\b";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String text = scanner.nextLine();
        Matcher matcher = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE).matcher(text);
        while (matcher.find()) {
            System.out.println(matcher.start() + " " + matcher.group());
        }
    }
}
