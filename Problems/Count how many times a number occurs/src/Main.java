import java.util.Arrays;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            int size = scanner.nextInt();
            int[] arr = new int[size];
            for (int i = 0; i < size; i++) {
                arr[i] = scanner.nextInt();
            }
            int n = scanner.nextInt();
            System.out.println(Arrays.stream(arr).boxed().filter(i -> i == n).count());
        }
    }
}
