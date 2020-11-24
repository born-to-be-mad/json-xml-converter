import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        LongStream longStream = IntStream.range(0, scanner.nextInt())
                                         .mapToLong(value -> scanner.nextInt());
        final AtomicLong max = new AtomicLong(Long.MIN_VALUE);
        boolean sorted = longStream.allMatch(n -> n >= max.getAndSet(n));
        System.out.println(sorted ? "true" : "false");

    }
}
