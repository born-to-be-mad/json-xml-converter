import java.util.Scanner;

class FixingArithmeticException {

    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            int a = scanner.nextInt();
            int b = scanner.nextInt();
            int c = scanner.nextInt();
            int d = scanner.nextInt();

            int result = a / ((b + c) / d);

            System.out.println(result);
        } catch (ArithmeticException e) {
            System.out.println("Division by zero!");
        }
    }
}
