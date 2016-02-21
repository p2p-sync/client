package org.rmatil.sync.client.command.init;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.io.Output;
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

    @Option(name = {"-p", "--path"}, title = "SyncFolderPath", arity = 1, description = "The path to the sync folder")
    @Required
    private String syncFolder;

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigPath", arity = 1, description = "The path to the application config")
    private String applicationConfigPath;

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {
            Path syncFolderPath = Paths.get(this.syncFolder).toAbsolutePath();
            try {
                // first create a default application config
                Path configDir;
                if (null == this.applicationConfigPath) {
                    configDir = Sync.createDefaultApplicationConfig();
                } else {
                    IValidator validator = new PathValidator(this.applicationConfigPath);

                    if (! validator.validate()) {
                        Output.println("Path " + this.applicationConfigPath + " does not exist");
                        return 1;
                    }

                    configDir = Sync.createDefaultApplicationConfig(Paths.get(this.applicationConfigPath));
                }

                // TODO: create public private key, if not yet existing

                if (null != this.syncFolder) {
                    IValidator validator = new PathValidator(this.syncFolder);

                    if (! validator.validate()) {
                        Output.println("Path " + syncFolderPath + " does not exist");
                        return 1;
                    }

                    Sync.init(syncFolderPath);

                    Output.println("Initialized sync directory at " + syncFolderPath);
                    Output.println("Configuration directory is at " + configDir);
                }
            } catch (IOException e) {
                Output.println("Failed to initialise sync folder: " + e.getMessage());
                return 1;
            }
        }

        return 0;
    }
}
