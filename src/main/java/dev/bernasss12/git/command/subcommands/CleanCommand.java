package dev.bernasss12.git.command.subcommands;

import java.io.File;
import java.util.concurrent.Callable;

import picocli.CommandLine.Command;

@Command(
        name = "clean"
)
public class CleanCommand implements Callable<Void> {

    @Override
    public Void call() {
        File root = new File(".git");
        if (deleteFolder(root)) {
            System.out.println("Repository deleted successfully");
        }
        return null;
    }

    boolean deleteFolder(File folder) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteFolder(file);
                } else {
                    file.delete();
                }
            }
        }
        return folder.delete();
    }
}
