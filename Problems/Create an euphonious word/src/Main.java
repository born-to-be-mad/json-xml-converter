import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
    private static final String VOWELS =
        "(" + String.join("|", "aeiouy".split("")) + ")";
    private static final String CONSONANTS =
        "(" + String.join("|", "bcdfghjklmnpqrstvwxz".split("")) + ")";

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Pattern pattern = Pattern.compile("\\b", Pattern.CASE_INSENSITIVE);

            for (String word : Arrays.asList("schedule",
                                             "garage",
                                             "player",
                                             "biiiiig")) {
                String transformedWord = transform(word);
                System.out.println(word + " -> " + transformedWord + " -> delta:" + countDifference(transformedWord));

            }
            // Using Regex Positive Lookahead to validate if two vowels or consonats in a row also have an extra following vowel / consonant.
            // Then count the regex Pattern matches.
            String in = new Scanner(System.in).nextLine().toLowerCase();
            Pattern p = Pattern.compile("(?=[b-df-hj-np-tv-xz]{3})[b-df-hj-np-tv-xz]{2}|(?=[aeiouy]{3})[aeiouy]{2}");
            System.out.println(p.matcher(in).results().count());
        }
    }

    private static String transform(String word) {
        return word.replaceAll(VOWELS, "V")
                   .replaceAll(CONSONANTS, "C");
    }

    private static long countDifference(String word) {
        long vovels = word.chars().filter(ch -> ch == 'V').count();
        long consonants = word.chars().filter(ch -> ch == 'C').count();
        return (consonants - vovels);
    }
}
