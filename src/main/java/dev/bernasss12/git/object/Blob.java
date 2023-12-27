package dev.bernasss12.git.object;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;

public record Blob(String contents) implements GitObject {

    public static Blob fromBytes(byte[] bytes) {
        return new Blob(new String(bytes));
    }

    /**
     * Generates a deflated and formatted blob from the file that it's been given.
     *
     * @param path file that will be converted to git file.
     */
    public static Blob readBlobFromFile(Path path) {
        try (FileInputStream input = new FileInputStream(path.toFile())) {
            byte[] contents = input.readAllBytes();
            return Blob.fromBytes(contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] toBytes() {
        return ("blob " + contents.length() + "\0" + contents).getBytes();
    }

    @Override
    public String getContentAsString() {
        return contents;
    }

    @Override
    public String getType() {
        return "blob";
    }

}
