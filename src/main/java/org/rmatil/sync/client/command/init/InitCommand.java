package org.rmatil.sync.client.command.init;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "init", description = "Initializes the application with a default configuration")
public class InitCommand implements ICliRunnable {

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
    private HelpOption<InitCommand> help;

    @Option(name = {"-p", "--path"}, title = "SyncFolderPath", description = "The path to the sync folder")
    @Required
    private String syncFolder;

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {
            Path syncFolderPath = Paths.get(this.syncFolder).toAbsolutePath();
            try {
                // first create a default application config
                Path configDir = Sync.createDefaultApplicationConfig();

                if (null != this.syncFolder) {
                    IValidator validator = new PathValidator(this.syncFolder);

                    if (! validator.validate()) {
                        System.out.println("Path " + syncFolderPath + " does not exist");
                        return 1;
                    }

                    Sync.init(syncFolderPath);

                    System.out.println("Initialized sync directory at " + syncFolderPath);
                    System.out.println("Configuration directory is at " + configDir);
                }
            } catch (IOException e) {
                System.out.println("Failed to initialise sync folder: " + e.getMessage());
                return 1;
            }
        }

        return 0;
    }
}
