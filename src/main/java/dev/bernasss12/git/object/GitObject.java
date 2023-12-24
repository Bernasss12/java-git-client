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
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import dev.bernasss12.git.util.ArrayUtils;

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
                default -> throw new IllegalArgumentException("\"" + type + " is not a supported git file type.");
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void writeToFile(GitObject object) {
        File file = ROOT.resolve(pathFromHash(object.getHash())).toFile();
        file.getParentFile().mkdirs();
        try (final DeflaterOutputStream deflater = new DeflaterOutputStream(new FileOutputStream(file))) {
            deflater.write(object.toBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Path pathFromHash(String hash) {
        return Paths.get(hash.substring(0, 2), hash.substring(2));
    }

    private static String hashFromPath(Path path) {
        String start = path.getParent().getFileName().toString();
        String end = path.getFileName().toString();
        return start + end;
    }

    String getContentAsString();

    byte[] toBytes();

    default String getHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(toBytes());
            return ArrayUtils.byteArrayToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
