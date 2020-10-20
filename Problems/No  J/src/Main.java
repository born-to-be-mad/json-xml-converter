import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class Main {

    public static void processIterator(String[] array) {
        List<String> strings = new ArrayList<>(Arrays.asList(array));
        ListIterator<String> iterator = strings.listIterator();
        while (iterator.hasNext()) {
            String current = iterator.next();
            if (current.startsWith("J")) {
                strings.set(iterator.previousIndex(), current.substring(1));
            } else {
                iterator.remove();
            }
        }
        Collections.reverse(strings);
        strings.forEach(System.out::println);
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        processIterator(scanner.nextLine().split(" "));
    }
}
