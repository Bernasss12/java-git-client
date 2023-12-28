package dev.bernasss12.git.object;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Objects;

import dev.bernasss12.git.util.ArrayUtils;

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
        return new Tree(entries.stream().sorted(Comparator.comparing(entry -> entry.file)).toList());
    }

    public static Tree fromPath(boolean write) {
        return fromPath(Paths.get("").toAbsolutePath(), write);
    }

    public static Tree fromPath(Path path, boolean write) {
        File tree = path.toFile();
        if (!tree.exists() || !tree.isDirectory()) {
            throw new InvalidParameterException("Path must point to a real directory.");
        }
        File[] children = tree.listFiles();
        List<Entry> entries = new ArrayList<>();
        for (File child : children) {
            EntryMode mode;
            if (child.getName().startsWith(".")) continue; // TODO implement gitignore at some point
            if (child.isDirectory()) {
                mode = EntryMode.DIRECTORY;
                Tree childTree = Tree.fromPath(child.toPath(), write);
                entries.add(
                        new Entry(
                                mode,
                                childTree,
                                child.getName()
                        )
                );
            } else {
                mode = child.canExecute() ?
                        EntryMode.REGULAR_EXECUTABLE :
                        child.canWrite() ?
                                EntryMode.REGULAR_NON_EXECUTABLE_GROUP_WRITABLE :
                                EntryMode.REGULAR_NON_EXECUTABLE;
                Blob childBlob = Blob.readBlobFromFile(child.toPath());
                entries.add(
                        new Entry(
                                mode,
                                childBlob,
                                child.getName()
                        )
                );
            }
        }
        Tree generated = new Tree(entries.stream().sorted(Comparator.comparing(entry -> entry.file)).toList());
        if (write) {
            GitObject.writeToFile(generated);
        }
        return generated;
    }

    /**
     * Writes the whole tree recursively. Unused for the challenge.
     *
     * @param sb    string builder for adding information recursively to the result.
     * @param level indentation level representing relationship with adjacent lines.
     * @return the full tree starting from when the method was called with a null string builder param.
     */
    public String getFullTree(StringBuilder sb, int level) {
        if (sb == null) {
            sb = new StringBuilder();
        }
        for (Entry e : entries) {
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
        List<byte[]> content = entries.stream().map(Entry::toBytes).toList();
        int length = content.stream().map(it -> it.length).reduce(0, Integer::sum);
        byte[] result = new byte[length];
        ByteBuffer buffer = ByteBuffer.wrap(result);
        content.forEach(buffer::put);
        return buffer.array();
    }

    public enum EntryMode {
        DIRECTORY(40000),
        REGULAR_NON_EXECUTABLE(100644),
        REGULAR_NON_EXECUTABLE_GROUP_WRITABLE(100664),
        REGULAR_EXECUTABLE(100755),
        SYMBOLIC_LINK(120000),
        GITLINK(160000);

        final int mode;

        EntryMode(int mode) {
            this.mode = mode;
        }

        static EntryMode fromMode(int mode) {
            for (EntryMode m : values()) {
                if (m.mode == mode) {
                    return m;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return String.format("%06d", mode);
        }
    }

    public static class Entry {

        private final EntryMode permissions;
        private final String hash;
        public final String file;
        private final String type;

        private final GitObject obj;

        public Entry(int permissions, byte[] hashBytes, String file) {
            this.permissions = EntryMode.fromMode(permissions);
            this.hash = HexFormat.of().formatHex(hashBytes);
            this.file = file;
            this.obj = GitObject.readFromHash(hash);
            this.type = this.obj == null ? (this.permissions == EntryMode.DIRECTORY ? "tree" : "blob") : this.obj.getType();
        }

        public Entry(EntryMode mode, GitObject obj, String file) {
            this.permissions = mode;
            this.obj = obj;
            this.hash = obj.getHash();
            this.file = file;
            this.type = obj.getType();
        }

        public byte[] toBytes() {
            byte[] meta = String.format("%d %s", permissions.mode, file).getBytes();
            byte[] hashBytes = HexFormat.of().parseHex(hash);
            byte[] result = new byte[meta.length + hashBytes.length + 1];
            ByteBuffer bb = ByteBuffer.wrap(result);
            bb.put(meta);
            bb.put((byte) 0);
            bb.put(hashBytes);
            return bb.array();
        }

        @Override
        public String toString() {
            return String.format("%s %s %s    %s", permissions, type, hash, file);
        }
    }
}
