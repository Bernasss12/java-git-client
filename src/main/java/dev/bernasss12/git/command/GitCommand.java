package dev.bernasss12.git.command;

import picocli.CommandLine;

@CommandLine.Command(
        name = "git",
        subcommands = {
                InitCommand.class
        }
)
public class GitCommand {}
