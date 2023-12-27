package dev.bernasss12.git.object;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import dev.bernasss12.git.util.ArrayUtils;
import static dev.bernasss12.git.util.ArrayUtils.byteArrayToHexString;

public record Tree(List<Entry> entries) implements GitObject {

    public static Tree fromBytes(byte[] content) {
        byte[] remaining = content;
        ArrayList<Entry> entries = new ArrayList<>();
        do {
            int nullByte = ArrayUtils.indexOf(remaining, '\0');
            int lastByte = nullByte + 21;
            String[] meta = new String(Arrays.copyOf(remaining, nullByte)).split(" ");
            byte[] hash = Arrays.copyOfRange(remaining, nullByte + 1, lastByte);
            entries.add(new Entry(Integer.parseInt(meta[0]), hash, meta[1]));
            remaining = Arrays.copyOfRange(remaining, lastByte, remaining.length);
        } while (remaining.length != 0);
        return new Tree(entries);
    }

    /**
     * Writes the whole tree recursively.
     * @param sb string builder for adding information recursively to the result.
     * @param level indentation level representing relationship with adjacent lines.
     * @return the full tree starting from when the method was called with a null string builder param.
     */
    public String getFullTree(StringBuilder sb, int level) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        for(Entry e : entries) {
            sb.append("  ".repeat(level));
            if (level > 0) {
                sb.append("- ");
            }
            sb.append(e.file);
            if (Objects.equals(e.type, "tree")) {
                sb.append("/\n");
                ((Tree) e.obj).getFullTree(sb, ++level);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    @Override
    public String getContentAsString() {
        StringBuilder sb = new StringBuilder();
        entries.forEach(it -> sb.append(it.toString()).append("\n"));
        return sb.toString();
    }

    @Override
    public String getType() {
        return "tree";
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    public static class Entry {

        private final int permissions;
        private final String hash;
        public final String file;
        private String type;

        private GitObject obj;

        public Entry(int permissions, byte[] hashBytes, String file) {
            this.permissions = permissions;
            this.hash = byteArrayToHexString(hashBytes);
            this.file = file;
            try {
                obj = GitObject.readFromHash(hash);
                this.type = obj.getType();
            } catch (Exception ignored) {}
        }

        @Override
        public String toString() {
            return String.format("%06d %s %s    %s", permissions, type, hash, file);
        }
    }
}
