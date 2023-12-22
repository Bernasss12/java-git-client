import dev.bernasss12.git.command.GitCommand;
import picocli.CommandLine;

void main(String[] args) {
    new CommandLine(new GitCommand()).execute(args);
}