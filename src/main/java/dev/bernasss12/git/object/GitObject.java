package dev.bernasss12.git.object;

public interface GitObject {

    static GitObject fromHash(String hash) {
        RawObject obj = RawObject.fromHash(hash);
        return fromRawObject(obj);
    }

    private static GitObject fromRawObject(RawObject object) {
        return switch (object.type) {
            case "blob" -> new GitBlobObject(object);
            default -> throw new IllegalArgumentException(STR."\"\{object.type}\" is not a supported git file type.");
        };
    }

    String getContentAsString();
}
