import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int m = scanner.nextInt();
        int p = scanner.nextInt();
        int k = scanner.nextInt();
        int i = 0;
        for (double temp = m; temp < k; ) {
            temp = temp * (1 + (double) p / 100);
            i++;
        }
        System.out.println(i);
    }
}
