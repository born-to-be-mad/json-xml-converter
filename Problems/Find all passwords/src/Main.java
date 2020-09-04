import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Main {

    public static final String PATTERN = "\\bpassword[\\s:]+\\b";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String text = scanner.nextLine();
        Pattern pattern = Pattern.compile(PATTERN, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        boolean found = false;
        while (matcher.find()) {
            String res = text.substring(matcher.end());
            System.out.println(res.split("[.\\s]")[0]);
            found = true;
        }
        if (!found) {
            System.out.println("No passwords found.");
        }
    }
}
