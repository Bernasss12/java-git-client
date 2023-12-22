package dev.bernasss12.git.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.zip.InflaterInputStream;

import dev.bernasss12.git.util.ArrayUtils;

public class GitObject {

    final Path path;
    final String hash;
    private Content content;

    private GitObject(Path path, String hash) {
        this.path = path;
        this.hash = hash;
    }

    public static GitObject fromPath(Path path) {
        String start = path.getParent().getFileName().toString();
        String end = path.getFileName().toString();
        return new GitObject(path, start + end);
    }

    public static GitObject fromHash(String hash) {
        Path path = Paths.get("objects", hash.substring(0, 2), hash.substring(2));
        return new GitObject(path, hash);
    }

    private String getType() {
        Content content = getContent();
    }

    public Content getContent() {
        if (content == null) {
            try (final InflaterInputStream in = new InflaterInputStream(new FileInputStream(path.toFile()))) {
                final byte[] input = in.readAllBytes();
                final int delimiter = ArrayUtils.indexOf(input, '\0');
                final String meta = new String(Arrays.copyOf(input, delimiter));
                final ByteBuffer content = ByteBuffer.wrap(Arrays.copyOfRange(input, delimiter, input.length - 1));
                this.content = new Content(meta, content);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return this.content;
    }

    public record Content(String metadata, ByteBuffer content) {}
}
