package dev.bernasss12.git.util;

public class ArrayUtils {

    private static final char[] HEXADECIMAL_CHARACTERS = "0123456789abcdef".toCharArray();

    public static int indexOf(byte[] bytes, char delimiter) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == delimiter) return i;
        }
        return -1;
    }

    public static String byteArrayToHexString(byte[] array) {
        StringBuilder result = new StringBuilder();
        for (byte b : array) {
            int h1 = (b >>> 4) & 0xf;
            int h2 = b & 0xf;
            char hex1 = HEXADECIMAL_CHARACTERS[h1];
            char hex2 = HEXADECIMAL_CHARACTERS[h2];
            result.append(hex1);
            result.append(hex2);
        }
        return result.toString();
    }
}
