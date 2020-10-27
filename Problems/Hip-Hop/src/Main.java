import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void iterateOverList(ListIterator<String> iter) {
        while (iter.hasNext()) {
            if (iter.next().contains("Hip")) {
                iter.add("Hop");
            }
        }
    }

    public static void printList(ListIterator<String> iter) {
        while (iter.hasNext()) {
            System.out.println(iter.next());
        }
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> list = Arrays.stream(scanner.nextLine().split(" ")).collect(Collectors.toList());
        iterateOverList(list.listIterator());
        printList(list.listIterator());
    }
}
