package dev.bernasss12.git.object;

import java.nio.file.Path;
import java.nio.file.Paths;

import dev.bernasss12.git.object.GitObject;
import org.junit.jupiter.api.Test;

class GitObjectTest {

    Path path = Paths.get("objects", "ab", "cdefghijkml");
    String hash = "abcdefghijkml";

    @Test
    void testFromHash() {
        GitObject obj = GitObject.fromHash(hash);
        assert obj.path.equals(path);
    }

    @Test
    void testFromFile() {
        GitObject obj = GitObject.fromPath(path);
        assert obj.hash.equals(hash);
    }
}