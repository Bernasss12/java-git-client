package dev.bernasss12.git.command;

import java.util.concurrent.Callable;

import dev.bernasss12.git.object.GitObject;
import picocli.CommandLine.*;

@Command(
        name = "cat-file",
        description = "Provide contents or details of repository objects."
)
public class CatFileCommand implements Callable<Integer> {

    @Parameters
    private String hash;

    @Option(
            names = { "-p" },
            description = "Pretty-print the contents of <object> based on its type."
    )
    private boolean prettyPrint;

    @Override
    public Integer call() {
        if (prettyPrint) {
            GitObject obj = GitObject.readFromHash(hash);
            System.out.print(obj.getContentAsString());
        }
        return null;
    }
}
