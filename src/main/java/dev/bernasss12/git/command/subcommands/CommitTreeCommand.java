package dev.bernasss12.git.command.subcommands;

import java.util.concurrent.Callable;

import dev.bernasss12.git.object.Commit;
import dev.bernasss12.git.object.GitObject;
import picocli.CommandLine;

@CommandLine.Command(
        name = "commit-tree"
)
public class CommitTreeCommand implements Callable<Void> {
    @CommandLine.Parameters(
            paramLabel = "<tree-sha>"
    )
    private String tree;

    @CommandLine.Option(
            names = "-p",
            paramLabel = "<commit-sha>"
    )
    private String parent;

    @CommandLine.Option(
            names = "-m",
            paramLabel = "<message>",
            required = true
    )
    private String message;

    @Override
    public Void call() {
        Commit commit = Commit.commit(tree, parent, message);
        GitObject.writeToFile(commit);
        System.out.println(commit.getHash());
        return null;
    }
}
