import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int size = Integer.parseInt(scanner.nextLine());
            String numbers = scanner.nextLine();
            String inputNumbersAsString = scanner.nextLine();
            System.out.println(searchNumbers(numbers, Arrays.asList(inputNumbersAsString.split(" "))));
        }
    }

    private static boolean searchNumbers(String numbers, List<String> searchNumbers) {
        String number1 = searchNumbers.get(0);
        String number2 = searchNumbers.get(1);
        int number1Index = numbers.indexOf(number1);
        while (number1Index != -1) {
            int number2Index = numbers.indexOf(number2);
            while (number2Index != -1) {
                if ((number1Index + 2 == number2Index) || (number2Index + 2 == number1Index)) {
                    return true;
                }
                number2Index = numbers.indexOf(number2, number2Index + 1);
            }
            number1Index = numbers.indexOf(number1, number1Index + 1);
        }
        return false;
    }
}
