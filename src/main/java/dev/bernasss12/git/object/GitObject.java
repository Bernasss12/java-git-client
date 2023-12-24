package dev.bernasss12.git.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.InflaterInputStream;

import dev.bernasss12.git.util.ArrayUtils;

public interface GitObject {

    Path ROOT = Paths.get(".git", "objects");

    /**
     * Finds the git file path by its hash, reads/inflates its contents, determines the type of file and creates an object representing that file.
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
            writeFromPath(Paths.get(""));
            return switch (type) {
                case "blob" -> Blob.fromBytes(hash, content);
                default -> throw new IllegalArgumentException("\"" + type + " is not a supported git file type.");
            };
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a deflated and formatted git file from the file that it's been given.
     * @param path file that will be converted to git file.
     */
    static String writeFromPath(Path path) {
        Blob blob = new Blob("asdada", "testcontenteasdaon asudausdasudn");
        byte[] bytes = blob.toBytes();
        return "";
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
}
