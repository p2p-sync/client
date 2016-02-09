package org.rmatil.sync.client.command;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Arguments;
import com.github.rvesse.airline.annotations.Command;
import org.apache.commons.lang3.StringUtils;
import org.rmatil.sync.client.command.config.SetConfigCommand;
import org.rmatil.sync.client.executor.CommandExecutor;

import javax.inject.Inject;
import java.util.List;

@Command(name = "showerror", description = "display a catched error message", hidden = true)
public class ShowErrorCommand implements ICliRunnable {

    /**
     * The special {@link HelpOption} provides a {@code -h} and {@code --help}
     * option that can be used to request that help be shown.
     * <p>
     * Developers need to check the {@link HelpOption#showHelpIfRequested()}
     * method which will display help if requested and return {@code true} if
     * the user requested the help
     * </p>
     */
    @Inject
    private HelpOption<ShowErrorCommand> help;

    @Arguments
    private List<String> errorMessages;

    public static void main(String[] args) {
        CommandExecutor.executeSingleCommand(SetConfigCommand.class, args);
    }

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {
            if (null != errorMessages)
                System.out.println("" + StringUtils.join(errorMessages, "\r\n"));
        }

        return 0;
    }
}
