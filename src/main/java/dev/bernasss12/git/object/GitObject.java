package dev.bernasss12.git.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import dev.bernasss12.git.util.ArrayUtils;
import dev.bernasss12.git.util.ByteArrayBuilder;

public interface GitObject {

    Path ROOT = Paths.get(".git", "objects");

    /**
     * Finds the git file path by its hash, reads/inflates its contents, determines the type of file and creates an object representing that file.
     *
     * @param hash SHA-1 hash generated from the original file.
     * @return GitObject with all the data the git file has.
     */
    static GitObject readFromHash(String hash) {
        try (final InflaterInputStream inflater = new InflaterInputStream(new FileInputStream(ROOT.resolve(pathFromHash(hash)).toFile()))) {
            byte[] data = inflater.readAllBytes();
            final int delimiter = ArrayUtils.indexOf(data, '\0');
            final List<String> meta = Arrays.stream(new String(Arrays.copyOf(data, delimiter)).split(" ")).toList();
            byte[] content = Arrays.copyOfRange(data, delimiter + 1, data.length);
            String type = meta.getFirst();
            return switch (type) {
                case "blob" -> Blob.fromBytes(content);
                case "tree" -> Tree.fromBytes(content);
                case "commit" -> Commit.fromBytes(content);
                default -> throw new IllegalArgumentException("\"" + type + "\" is not a supported git file type.");
            };
        } catch (FileNotFoundException e) {
            return null;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Tries to read the file from hash.
     *
     * @param hash Hash of file contents
     * @return The object read from the disk or null if the types don't match.
     */
    static <T extends GitObject> T readFromHashAs(String hash) {
        try {
            return (T) readFromHash(hash);
        } catch (ClassCastException e) {
            System.err.println(hash + " is not of expected type.");
            return null;
        }
    }

    static void writeToFile(GitObject object) {
        File file = ROOT.resolve(pathFromHash(object.getHash())).toFile();
        // If the file already exists, there is no point overriding it as it's very unlikely this is a hash collision.
        if (file.exists()) return;
        if (!file.getParentFile().mkdirs()) {
            if (!file.getParentFile().exists()) {
                System.err.printf("Unable to create parent file: %s\n", file.getParent());
            }
        }
        try (final DeflaterOutputStream deflater = new DeflaterOutputStream(new FileOutputStream(file))) {
            deflater.write(contentWithHeader(object));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] contentWithHeader(GitObject object) {
        byte[] content = object.toBytes();
        byte[] header = header(object.getType(), content.length);
        ByteArrayBuilder buffer = new ByteArrayBuilder();
        buffer.put(header);
        buffer.put(content);
        return buffer.getArray();
    }

    private static byte[] header(String type, int length) {
        ByteArrayBuilder builder = new ByteArrayBuilder();
        builder.puts(type);
        builder.put((byte) ' ');
        builder.puts(String.valueOf(length));
        builder.put((byte) '\0');
        return builder.getArray();
    }

    private static Path pathFromHash(String hash) {
        return Paths.get(hash.substring(0, 2), hash.substring(2));
    }

    String getContentAsString();

    String getType();

    byte[] toBytes();

    default String getHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(contentWithHeader(this));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
