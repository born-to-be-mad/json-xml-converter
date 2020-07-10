import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {

            String option = scanner.next();
            switch (option) {
            case "triangle":
                double a = scanner.nextDouble();
                double b = scanner.nextDouble();
                double c = scanner.nextDouble();
                double p = (a + b + c) / 2;
                double square = Math.sqrt(p * (p - a) * (p - b) * (p - c));
                System.out.println(square);
                break;
            case "rectangle":
                System.out.println(scanner.nextDouble() * scanner.nextDouble());
                break;
            case "circle":
                double r = scanner.nextDouble();
                System.out.println(3.14 * r * r);
                break;
            default:
                System.out.println("Unknown form");
                break;
            }

        }
    }
}
