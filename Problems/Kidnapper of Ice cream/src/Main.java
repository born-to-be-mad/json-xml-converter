import java.util.*;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Collection<String> source = new ArrayList<>(Arrays.asList(scanner.nextLine().split("\\s+")));
        Collection<String> dest = Arrays.asList(scanner.nextLine().split("\\s+"));

        boolean result = dest.stream().allMatch(source::remove);

        System.out.println(result ? "You get money" : "You are busted");
    }
}
