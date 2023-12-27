package dev.bernasss12.git.command;

import picocli.CommandLine;

@CommandLine.Command(
        name = "git",
        subcommands = {
                InitCommand.class,
                CatFileCommand.class,
                CleanCommand.class,
                HashObjectCommand.class,
                LsTreeCommand.class
        }
)
public class GitCommand {}
