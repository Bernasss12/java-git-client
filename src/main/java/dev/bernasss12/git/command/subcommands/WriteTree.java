package dev.bernasss12.git.command.subcommands;

import java.util.concurrent.Callable;

import dev.bernasss12.git.object.GitObject;
import dev.bernasss12.git.object.Tree;
import picocli.CommandLine;

@CommandLine.Command(
        name = "write-tree"
)
public class WriteTree implements Callable<Void> {

    @Override
    public Void call() {
        Tree root = Tree.fromPath(true);
        GitObject.writeToFile(root);
        System.out.println(root.getHash());
        GitObject read = GitObject.readFromHashAs(root.getHash());
        System.err.print(root.getContentAsString());
        System.err.print(read.getContentAsString());
        return null;
    }
}
