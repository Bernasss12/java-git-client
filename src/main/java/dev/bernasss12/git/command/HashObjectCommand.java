package dev.bernasss12.git.command;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import dev.bernasss12.git.object.Blob;
import dev.bernasss12.git.object.GitObject;
import picocli.CommandLine.*;

@Command(
        name = "hash-object"
)
public class HashObjectCommand implements Callable<Void> {
    @Option(
            names = { "-w" },
            description = "Actually write the object into the object database."
    )
    private boolean write;

    @Parameters(
            paramLabel = "<file>",
            index = "0"
    )
    private Path file;

    @Override
    public Void call() {
        Blob blob = Blob.readBlobFromFile(file);
        System.out.println(blob.getHash());
        if (write) {
            GitObject.writeToFile(blob);
            return null;
        }
        return null;
    }
}
