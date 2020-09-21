import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println(scanner.nextDouble() * 10.5
                           + scanner.nextDouble() * 4.4
                           + (scanner.nextDouble() + scanner.nextDouble()) / 2.2);
    }
}
