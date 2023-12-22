package dev.bernasss12.git.object;

public record Blob(String hash, String contents) implements GitObject {

    public static Blob fromBytes(String hash, byte[] bytes) {
        return new Blob(hash, new String(bytes));
    }

    public byte[] toBytes() {
        return STR."blob \{contents.length()}\0\{contents}".getBytes();
    }

    @Override
    public String getContentAsString() {
        return contents;
    }
}
