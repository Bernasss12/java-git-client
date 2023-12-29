package dev.bernasss12.git.util;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;

public class ArrayUtils {

    public static int indexOf(byte[] bytes, char delimiter) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == delimiter) return i;
        }
        return -1;
    }

    public static String findStartingWith(String[] strings, String prefix) {
        Optional<String> result = Arrays.stream(strings)
                .filter(it -> it.startsWith(prefix))
                .findFirst();
        return result.orElse(null);
    }

    public static String[] findAllStartingWith(String[] strings, String prefix) {
        return Arrays.stream(strings)
                .filter(it -> it.startsWith(prefix))
                .toArray(String[]::new);
    }

    public static int indexOfMatching(String[] lines, Predicate<String> predicate) {
        for (int i = 0; i < lines.length; i++) {
            if (predicate.test(lines[i])) {
                return i;
            }
        }
        return -1;
    }

    public static String[] subarray(String[] lines, int begin) {
        return Arrays.copyOfRange(lines, begin, lines.length);
    }
}
