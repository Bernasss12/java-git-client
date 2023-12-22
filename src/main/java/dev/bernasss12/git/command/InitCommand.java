package dev.bernasss12.git.command;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(name = "init", description = "Create an empty Git repository or reinitialize an existing one.")
public class InitCommand implements Callable<Void> {

    @Override
    public Void call() {
        final File root = new File(".git");
        if (root.exists()) {
            System.out.println("Git repository already exists in this location.");
        }
        new File(root, "objects").mkdirs();
        new File(root, "refs").mkdirs();
        final File head = new File(root, "HEAD");

        try {
            head.createNewFile();
            Files.write(head.toPath(), "ref: refs/heads/master\n".getBytes());
            System.out.println("Initialized git directory");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
