package dev.bernasss12.git.object;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dev.bernasss12.git.util.ArrayUtils;

public record Blob(String contents) implements GitObject {

    public static Blob fromBytes(byte[] bytes) {
        return new Blob(new String(bytes));
    }

    @Override
    public byte[] toBytes() {
        return ("blob " + contents.length() + "\0" + contents).getBytes();
    }

    @Override
    public String getHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] hash = digest.digest(toBytes());
            return ArrayUtils.byteArrayToHexString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getContentAsString() {
        return contents;
    }

}
