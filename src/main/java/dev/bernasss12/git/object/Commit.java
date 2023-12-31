package dev.bernasss12.git.object;

import java.util.Arrays;

import static dev.bernasss12.git.util.ArrayUtils.findAllStartingWith;
import static dev.bernasss12.git.util.ArrayUtils.findStartingWith;
import static dev.bernasss12.git.util.ArrayUtils.indexOfMatching;
import static dev.bernasss12.git.util.ArrayUtils.subarray;
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

    public static Commit fromBytes(byte[] bytes) {
        String[] lines = new String(bytes).split("\n");

        String tree = findStartingWith(lines, "tree").substring(5);
        String[] parents = Arrays.stream(findAllStartingWith(lines, "parent")).map(it -> it.substring(7)).toArray(String[]::new);
        String author = findStartingWith(lines, "author");
        String committer = findStartingWith(lines, "committer");
        String[] message = subarray(lines, indexOfMatching(lines, String::isEmpty) + 1);

        return new Commit(
                tree,
                parents,
                Identity.extract(author),
                Identity.extract(committer),
                Timestamp.extract(author),
                Timestamp.extract(committer),
                message
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
