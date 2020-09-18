import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int lines = scanner.nextInt();
        Set<String> dictionaryWords = new HashSet<>();
        for (int i = 0; i <= lines; i++) {
            dictionaryWords.add(scanner.nextLine().toLowerCase());
        }
        int textLines = scanner.nextInt();
        Set<String> words = new LinkedHashSet<>();
        for (int i = 0; i <= textLines; i++) {
            words.addAll(SetUtils.getSetFromString(scanner.nextLine()));
        }

        words.removeIf(w -> dictionaryWords.contains(w.toLowerCase()));
        words.forEach(System.out::println);
    }
}

class SetUtils {

    public static Set<String> getSetFromString(String str) {
        return new HashSet<>(Arrays.asList(str.split(" ")));
    }

}
