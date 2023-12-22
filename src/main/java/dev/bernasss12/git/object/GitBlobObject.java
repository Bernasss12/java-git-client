package dev.bernasss12.git.object;

public class GitBlobObject implements GitObject {

    final RawObject raw;

    GitBlobObject(RawObject object) {
        this.raw = object;
    }

    @Override
    public String getContentAsString() {
        return new String(raw.content);
    }
}
