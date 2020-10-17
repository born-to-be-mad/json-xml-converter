import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int printed = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= i && printed < n; j++) {
                System.out.print(i + " ");
                printed++;
            }
        }
    }
}
