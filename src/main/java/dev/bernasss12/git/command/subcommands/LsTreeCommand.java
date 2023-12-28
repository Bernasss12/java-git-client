package dev.bernasss12.git.command.subcommands;

import java.util.concurrent.Callable;

import dev.bernasss12.git.object.GitObject;
import dev.bernasss12.git.object.Tree;
import picocli.CommandLine;

@CommandLine.Command(
        name = "ls-tree"
)
public class LsTreeCommand implements Callable<Void> {

    @CommandLine.Option(
            names = { "--name-only" }
    )
    private boolean nameOnly;

    @CommandLine.Parameters(
            paramLabel = "<sha-1>"
    )
    private String hash;

    @Override
    public Void call() {
        Tree tree = GitObject.readFromHashAs(hash);
        if (tree == null) {
            System.err.println("Error while getting tree from given hash.");
            return null;
        }
        //System.out.println(tree.getFullTree());
        if (nameOnly) {
            tree.entries().forEach(entry -> {
                System.out.println(entry.file);
            });
            return null;
        }
        return null;
    }
}
