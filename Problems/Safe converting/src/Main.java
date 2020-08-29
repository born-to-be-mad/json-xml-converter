import java.util.Scanner;

public class Main {

    public static int convert(Long val) {
        return val == null
               ? 0
               : val > Integer.MAX_VALUE
                 ? Integer.MAX_VALUE
                 : val < Integer.MIN_VALUE
                   ? Integer.MIN_VALUE
                   : val.intValue();
    }

    /* Do not change code below */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String val = scanner.nextLine();
        Long longVal = "null".equals(val) ? null : Long.parseLong(val);
        System.out.println(convert(longVal));
    }
}
