package org.rmatil.sync.client.help;

import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.model.GlobalMetadata;
import org.rmatil.sync.client.command.ICliRunnable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Command(name = "help", description = "A command that provides help on other commands")
public class Help implements ICliRunnable {

    @Inject
    private GlobalMetadata<ICliRunnable> global;

    @Arguments(description = "Provides the name of the commands you want to provide help for")
    private List<String> commandNames = new ArrayList<>();

    @Option(name = "--include-hidden", description = "When set hidden commands and options are shown in help", hidden = true)
    private boolean includeHidden = false;

    @Override
    public int run() {
        try {
            com.github.rvesse.airline.help.Help.help(global, commandNames, this.includeHidden);
        } catch (IOException e) {
            System.err.println("Failed to output help: " + e.getMessage());
            e.printStackTrace(System.err);
            return 1;
        }

        return 0;
    }

}
