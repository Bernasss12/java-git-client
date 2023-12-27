package dev.bernasss12.git.command.subcommands;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

import dev.bernasss12.git.object.GitObject;
import dev.bernasss12.git.object.Tree;
import picocli.CommandLine;

@CommandLine.Command(
        name = "write-tree"
)
public class WriteTree implements Callable<Void> {

    @Override
    public Void call(){
        Tree root = Tree.fromPath(Paths.get("").toAbsolutePath());
        GitObject.writeToFile(root);
        System.out.println(root.getHash());
        GitObject read = GitObject.readFromHashAs(root.getHash());
        return null;
    }
}
