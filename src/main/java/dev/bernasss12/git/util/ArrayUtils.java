package dev.bernasss12.git.util;

public class ArrayUtils {
    public static int indexOf(byte[] bytes, char delimiter) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == delimiter) return i;
        }
        return -1;
    }
}
