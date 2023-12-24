package dev.bernasss12.git.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.bernasss12.git.util.ArrayUtils;

public record Tree(List<Entry> entries) implements GitObject {

    public static Tree fromBytes(byte[] content) {
        byte[] remaining = content;
        ArrayList<Entry> entries = new ArrayList<>();
        do {
            int nullByte = ArrayUtils.indexOf(remaining, '\0');
            int lastByte = nullByte + 21;
            String meta = new String(Arrays.copyOf(remaining, nullByte));
            byte[] hash = Arrays.copyOfRange(remaining, nullByte + 1, lastByte);
            remaining = Arrays.copyOfRange(remaining, lastByte,remaining.length);
            // TODO actually finish this xD
        } while (remaining.length != 0);
        return null;
    }

    @Override
    public String getContentAsString() {
        // TODO string builder
        return null;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    public record Entry(int permissions, byte[] hash, String file){}
}
