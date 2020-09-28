import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int m = scanner.nextInt();
        int k = scanner.nextInt();
        boolean possible = k <= n * m && (k % n == 0 || k % m == 0);
        System.out.println(possible ? "YES" : "NO");
    }
}
