import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> listOfString = Arrays.asList(scanner.nextLine().split(" "));
        List<Integer> integerList = listOfString.stream()
                                                .map(Integer::parseInt)
                                                .collect(Collectors.toList());
        int number = scanner.nextInt();
        Collections.sort(integerList);
        Integer min = integerList.get(0);
        boolean exact = integerList.contains(number);
        List<Integer> outputList = integerList.stream()
                                              .filter(integer -> exact ? integer.equals(number)
                                                                       : Math.abs(number - integer) == min)
                                              .collect(Collectors.toList());

        if (outputList.isEmpty()) {
            System.out.println(integerList.get(integerList.size() - 1));
        } else {
            System.out.println(outputList.stream().map(String::valueOf).collect(Collectors.joining(" ")));
        }

    }
}
