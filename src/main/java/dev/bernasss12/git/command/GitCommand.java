package dev.bernasss12.git.command;

import dev.bernasss12.git.command.subcommands.CatFileCommand;
import dev.bernasss12.git.command.subcommands.CleanCommand;
import dev.bernasss12.git.command.subcommands.HashObjectCommand;
import dev.bernasss12.git.command.subcommands.InitCommand;
import dev.bernasss12.git.command.subcommands.LsTreeCommand;
import dev.bernasss12.git.command.subcommands.WriteTree;
import picocli.CommandLine;

@CommandLine.Command(
        name = "git",
        subcommands = {
                InitCommand.class,
                CatFileCommand.class,
                CleanCommand.class,
                HashObjectCommand.class,
                LsTreeCommand.class,
                WriteTree.class
        }
)
public class GitCommand {}
