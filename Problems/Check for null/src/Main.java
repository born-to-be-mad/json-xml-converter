// do not remove imports

import java.util.Arrays;
import java.util.Objects;

class ArrayUtils {
    public static <T> boolean hasNull(T[] array) {
        return Arrays.stream(array).anyMatch(Objects::isNull);
    }

    public static <T> T getFirst(T[] array) {
        return Arrays.stream(array).findFirst().orElse(null);
    }

    public static <T> T[] invert(T[] array) {
        int size = array.length;
        for (int i = 0; i < size / 2; i++) {
            T tmp = array[i];
            array[i] = array[size - i - 1];
            array[size - i - 1] = tmp;
        }
        return array;
    }
}
