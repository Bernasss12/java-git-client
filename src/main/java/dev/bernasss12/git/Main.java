import dev.bernasss12.git.command.GitCommand;
import dev.bernasss12.git.command.InitCommand;
import picocli.CommandLine;

void main(String[] args) {
    new CommandLine(new GitCommand()).execute(args);
}