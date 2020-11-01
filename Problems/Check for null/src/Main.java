// do not remove imports

import java.util.Arrays;
import java.util.Objects;

class ArrayUtils {
    public static <T> boolean hasNull(T[] array) {
        return Arrays.stream(array).anyMatch(Objects::isNull);
    }
}
