import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            String input = scanner.nextLine();
            LocalDate inputDate = LocalDate.parse(input);
            String outputDateAsString = DateTimeFormatter.ofPattern("MM/dd/YYYY").format(inputDate);
            System.out.println(outputDateAsString);
        }
    }
}
