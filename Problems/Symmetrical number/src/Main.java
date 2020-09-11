import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int number = scanner.nextInt();
        int a4 = number % 10;
        number = number / 10;
        int a3 = number % 10;
        number = number / 10;
        int a2 = number % 10;
        number = number / 10;
        int a1 = number % 10;
        if (a4 == a1 && a2 == a3) {
            System.out.println("1");
        } else {
            System.out.println("2");
        }
    }
}
