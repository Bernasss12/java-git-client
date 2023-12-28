package dev.bernasss12.git.object;

import java.io.File;
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
import dev.bernasss12.git.util.ByteArrayBuilder;
import dev.bernasss12.git.util.MultilineBuilder;

public record Tree(List<Entry> entries) implements GitObject {

    public static Tree fromBytes(byte[] content) {
        String str = new String(content);
        //System.out.println(str);
        byte[] remaining = Arrays.copyOf(content, content.length);
        ArrayList<Entry> entries = new ArrayList<>();
        do {
            int nullByte = ArrayUtils.indexOf(remaining, '\0');
            if (nullByte < 0) {
                System.err.println("Problem parsing tree.");
                return null;
            }
            int lastByte = nullByte + 21;
            String[] meta = new String(Arrays.copyOf(remaining, nullByte)).split(" ");
            byte[] hash = Arrays.copyOfRange(remaining, nullByte + 1, lastByte);
            entries.add(new Entry(meta[0], hash, meta[1]));
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
            throw new InvalidParameterException("Path must point to a real directory: " + path);
        }
        File[] children = tree.listFiles();
        List<Entry> entries = new ArrayList<>();
        for (File child : children) {
            EntryMode mode;
            if (child.getName().startsWith(".")) continue; // TODO implement gitignore at some point
            if (child.isDirectory()) {
                mode = EntryMode.DIRECTORY;
                if (Objects.requireNonNull(child.listFiles()).length == 0) continue;
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
                        EntryMode.REGULAR_NON_EXECUTABLE;
                Blob childBlob = Blob.readBlobFromFile(child.toPath());
                if (write) {
                    GitObject.writeToFile(childBlob);
                }
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
     * @param mb    string builder for adding information recursively to the result.
     * @param level indentation level representing relationship with adjacent lines.
     * @return the full tree starting from when the method was called with a null string builder param.
     */
    private String getFullTree(MultilineBuilder mb, int level) {
        if (mb == null) {
            mb = new MultilineBuilder();
        }
        for (Entry e : entries) {
            String indent = level > 0 ? "  ".repeat(level) + "- " : "";
            if (Objects.equals(e.type, "tree")) {
                mb.appendln(String.format("%s%s/: %s", indent, e.file, e.hash));
                ((Tree) e.obj).getFullTree(mb, level + 1);
            } else {
                mb.appendln(String.format("%s%s: %s", indent, e.file, e.hash));
            }
        }
        return mb.toString();
    }

    public String getFullTree() {
        return getFullTree(null, 0);
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
        ByteArrayBuilder buffer = new ByteArrayBuilder();
        content.forEach(buffer::put);
        return buffer.getArray();
    }

    public enum EntryMode {
        DIRECTORY("40000"),
        REGULAR_NON_EXECUTABLE("100644"),
        REGULAR_NON_EXECUTABLE_GROUP_WRITABLE("100664"),
        REGULAR_EXECUTABLE("100755"),
        SYMBOLIC_LINK("120000"),
        GITLINK("160000");

        final String mode;

        EntryMode(String mode) {
            this.mode = mode;
        }

        static EntryMode fromMode(String mode) {
            for (EntryMode m : values()) {
                if (Objects.equals(m.mode, mode)) {
                    return m;
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return mode;
        }
    }

    public static class Entry {

        private final EntryMode permissions;
        private final String hash;
        public final String file;
        private final String type;

        public final GitObject obj;

        public Entry(String permissions, byte[] hashBytes, String file) {
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
            byte[] hash = HexFormat.of().parseHex(this.hash);
            ByteArrayBuilder bytes = new ByteArrayBuilder();
            bytes.puts(permissions.mode);
            bytes.put((byte) ' ');
            bytes.puts(file);
            bytes.put((byte) '\0');
            bytes.put(hash);
            return bytes.getArray();
        }

        @Override
        public String toString() {
            return String.format("%s %s %s    %s", permissions, type, hash, file);
        }
    }
}
