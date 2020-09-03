import java.util.*;

class AsciiCharSequence implements CharSequence {
    private final byte[] value;

    AsciiCharSequence(byte[] value) {
        this.value = value;
    }

    @Override
    public int length() {
        return value.length;
    }

    @Override
    public char charAt(int index) {
        return (char) (value[index] & 0xff);
    }

    @Override
    public AsciiCharSequence subSequence(int start, int end) {
        return new AsciiCharSequence(Arrays.copyOfRange(value, start, end));
    }

    @Override
    public String toString() {
        return new String(value);
    }
}
