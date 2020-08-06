import java.util.Scanner;

class RemoveExtraSpacesProblem {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            String text = scanner.nextLine();
            System.out.println(text.replaceAll("\\s+", " "));
        }

    }
}
