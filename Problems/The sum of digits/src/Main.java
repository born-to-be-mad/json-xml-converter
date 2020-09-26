import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        int sum = (number / 100);
        number -= sum * 100;
        sum += number / 10 + number % 10;
        System.out.println(sum);
    }
}
