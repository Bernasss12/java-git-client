package dev.bernasss12.git.util;

public class ByteArrayBuilder {

    private byte[] array = new byte[0];

    public void put(byte[] bytes) {
        int nl = array.length + bytes.length;
        byte[] na = new byte[nl];
        System.arraycopy(array, 0, na, 0, array.length);
        System.arraycopy(bytes, 0, na, array.length, bytes.length);
        array = na;
    }

    public void put(byte b) {
        int nl = array.length + 1;
        byte[] na = new byte[nl];
        System.arraycopy(array, 0, na, 0, array.length);
        na[array.length] = b;
        array = na;
    }

    public void puts(String string) {
        put(string.getBytes());
    }

    public byte[] getArray() {
        return array;
    }
}
