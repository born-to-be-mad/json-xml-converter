import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static java.util.stream.Collectors.toMap;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        String word1 = scanner.nextLine();
        String word2 = scanner.nextLine();
        Map<Character, Integer> frequencies1 = countFrequence(word1.toLowerCase());
        Map<Character, Integer> frequencies2 = countFrequence(word2.toLowerCase());

        System.out.println(
            Objects.equals(frequencies1,frequencies2) ? "yes" : "no"
        );
    }

    private static Map<Character, Integer> countFrequence(String word1) {
        return word1.chars()
                    .boxed()
                    .collect(toMap(k -> (char) k.intValue(),
                                   v -> 1,         // default 1 occurence
                                   Integer::sum));
    }
}
