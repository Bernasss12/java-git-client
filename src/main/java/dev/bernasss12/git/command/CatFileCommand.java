package dev.bernasss12.git.command;

import java.util.concurrent.Callable;

import picocli.CommandLine;

@CommandLine.Command(
        name = "cat-file",
        description = "Provide contents or details of repository objects."
)
public class CatFileCommand implements Callable<Integer> {
    @CommandLine.Parameters
    private String hash;

    @CommandLine.Option(
            names = {"-p"},
            description = "Pretty-print the contents of <object> based on its type."
    )
    private boolean prettyPrint;

    @Override
    public Integer call() {
        if (prettyPrint) {

        }
        return null;
    }
}
