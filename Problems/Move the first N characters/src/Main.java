import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        int shift = scanner.nextInt();
        if (shift < str.length()) {
            String start = str.substring(shift);
            String end = str.substring(0, shift);
            str = start + end;
        }
        System.out.println(str);
    }
}
