import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int height = scanner.nextInt();
        int numberOfBridges = scanner.nextInt();
        int crashedAt = -1;
        for (int value = 1; value <= numberOfBridges; value++) {
            int bridgeHeight = scanner.nextInt();
            if (height <= bridgeHeight) {
                crashedAt = value;
                break;
            }
        }
        System.out.println(crashedAt == -1
                           ? "Will not crash"
                           : "Will crash on bridge " + crashedAt);

    }
}
