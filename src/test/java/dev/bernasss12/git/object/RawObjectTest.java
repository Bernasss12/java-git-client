package dev.bernasss12.git.object;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class RawObjectTest {

    Path path = Paths.get("objects", "ab", "cdefghijkml");
    String hash = "abcdefghijkml";

    @Test
    void testFromHash() {
        RawObject obj = RawObject.fromHash(hash);
        assert obj.path.equals(path);
    }

    @Test
    void testFromFile() {
        RawObject obj = RawObject.fromPath(path);
        assert obj.hash.equals(hash);
    }
}