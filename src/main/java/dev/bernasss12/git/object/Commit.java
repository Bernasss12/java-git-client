package dev.bernasss12.git.object;

import dev.bernasss12.git.util.Identity;
import dev.bernasss12.git.util.MultilineBuilder;
import dev.bernasss12.git.util.Timestamp;

public class Commit implements GitObject {

    private final String treeHash;
    private final String[] parents;
    private final Identity author;
    private final Identity committer;
    private final Timestamp authored;
    private final Timestamp committed;
    private final String[] message;

    public Commit(String treeHash, String[] parents, Identity author, Identity committer, Timestamp authored, Timestamp committed, String[] message) {
        this.treeHash = treeHash;
        this.parents = parents;
        this.author = author;
        this.committer = committer;
        this.authored = authored;
        this.committed = committed;
        this.message = message;
    }

    public static Commit commit(String treeHash, String parent, String message) {
        Identity id = Identity.global();
        Timestamp ts = Timestamp.now();
        return new Commit(
                treeHash,
                new String[] { parent },
                id,
                id,
                ts,
                ts,
                new String[] { message }
        );
    }

    @Override
    public String getContentAsString() {
        MultilineBuilder ml = new MultilineBuilder();
        ml.appendfln("tree %s", treeHash);
        for (String parent : parents) {
            ml.appendfln("parent %s", parent);
        }
        ml.appendfln("author %s %s", author, authored);
        ml.appendfln("committer %s %s", committer, committed);
        ml.appendln();
        for (String line : message) {
            ml.appendln(line);
        }
        return ml.toString();
    }

    @Override
    public String getType() {
        return "commit";
    }

    @Override
    public byte[] toBytes() {
        return getContentAsString().getBytes();
    }
}
