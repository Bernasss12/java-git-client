package dev.bernasss12.git.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.zip.InflaterInputStream;

import dev.bernasss12.git.util.ArrayUtils;

public class RawObject {

    public final Path path;
    public final String hash;
    public final byte[] content;
    public final String type;
    public final int length;

    private RawObject(Path path, String hash) {
        this.path = path;
        this.hash = hash;

        try (final InflaterInputStream in = new InflaterInputStream(new FileInputStream(path.toFile()))) {
            byte[] input = in.readAllBytes();
            final int delimiter = ArrayUtils.indexOf(input, '\0');
            final List<String> meta = Arrays.stream(new String(Arrays.copyOf(input, delimiter)).split(" ")).toList();
            type = meta.getFirst();
            length = Integer.parseInt(meta.getLast());
            content = Arrays.copyOfRange(input, delimiter + 1, input.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static RawObject fromPath(Path path) {
        String start = path.getParent().getFileName().toString();
        String end = path.getFileName().toString();
        return new RawObject(path, start + end);
    }

    public static RawObject fromHash(String hash) {
        Path path = Paths.get(".git", "objects", hash.substring(0, 2), hash.substring(2));
        return new RawObject(path, hash);
    }

    public void prettyPrint() {}
}
