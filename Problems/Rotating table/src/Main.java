import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        final Scanner scanner = new Scanner(System.in);
        final int rows = scanner.nextInt();
        final int cols = scanner.nextInt();
        List<List<Integer>> matrix = new ArrayList<>();
        for (int i = 0; i < rows; i++) {
            matrix.add(new ArrayList<>());
            for (int j = 0; j < cols; j++) {
                matrix.get(i).add(scanner.nextInt());
            }
        }
        final int rotateRows = scanner.nextInt();

        Collections.rotate(matrix, rotateRows);

        matrix.forEach(row -> {
            row.forEach(el -> System.out.print(el + " "));
            System.out.println();
        });
    }

}
