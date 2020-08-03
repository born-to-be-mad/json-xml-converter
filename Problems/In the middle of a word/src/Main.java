import java.util.*;
import java.util.regex.*;

public class Main {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String part = scanner.nextLine();
            String line = scanner.nextLine();

            Pattern pattern = Pattern.compile("\\B.*" + part + "\\B.*", Pattern.CASE_INSENSITIVE);
            System.out.println(pattern.matcher(line).find() ? "YES" : "NO");
        }
    }
}
