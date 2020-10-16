import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

class ListUtils {

    /**
     * It splits the passed list into a sequence of sublists with a predefined size
     */
    public static <T> Collection<List<T>> splitListIntoSubLists(List<T> list, int subListSize) {
        final AtomicInteger counter = new AtomicInteger();

        Map<Integer, List<T>> collect = list.stream()
                                            .collect(groupingBy(it -> counter.getAndIncrement() / subListSize));
        return collect.values();

    }
}

/* Please, do not modify code in this class */
public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);

        final String[] values = scanner.nextLine().split("\\s+");

        final List<Integer> list = Arrays.stream(values)
                                         .map(Integer::parseInt)
                                         .collect(toList());

        final int subListSize = Integer.parseInt(scanner.nextLine());

        final Collection<List<Integer>> subLists = ListUtils.splitListIntoSubLists(list, subListSize);

        subLists.forEach(subList -> {
            final String representation = subList.stream()
                                                 .map(Object::toString)
                                                 .collect(joining(" "));
            System.out.println(representation);
        });
    }
}
