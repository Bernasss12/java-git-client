package dev.bernasss12.git;

import dev.bernasss12.git.command.GitCommand;
import picocli.CommandLine;

class Main {
    void main(String[] args) {
        new CommandLine(new GitCommand()).execute(args);
    }
}
