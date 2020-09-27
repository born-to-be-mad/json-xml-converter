import java.util.*;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Integer> map = new HashMap<>();

        Arrays.stream(scanner.nextLine().toLowerCase().split(""))
              .forEach(letter -> map.merge(letter, 1, Integer::sum));
        Arrays.stream(scanner.nextLine().toLowerCase().split(""))
              .forEach(letter -> map.merge(letter, -1, Integer::sum));

        System.out.println(map.values().stream().mapToInt(Math::abs).sum());
    }
}
