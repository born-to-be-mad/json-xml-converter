import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        String result;
        switch (scanner.nextLine()) {
        case "gryffindor":
            result = "bravery";
            break;
        case "hufflepuff":
            result = "loyalty";
            break;
        case "slytherin":
            result = "cunning";
            break;
        case "ravenclaw":
            result = "intellect";
            break;

        default:
            result = "not a valid house";
            break;
        }
        System.out.println(result);
    }
}
