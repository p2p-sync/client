package org.rmatil.sync.client.command.clean;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.exception.ValidationException;
import org.rmatil.sync.client.util.FileUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.config.Config;

import javax.inject.Inject;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "clean", description = "Clean up created files")
public class CleanCommand implements ICliRunnable {

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
    private HelpOption<CleanCommand> help;

    @Option(name = {"-l", "--clean-all"}, title = "CleanAll", arity = 1, description = "Remove the configuration directory as well as the object store from the specified synced folder")
    private String cleanAll;

    @Option(name = {"-c", "--clean"}, title = "CleanConfig", description = "Remove the configuration directory")
    private boolean cleanConfig;

    @Option(name = {"-o", "--clean-os"}, title = "CleanObjectStore", arity = 1, description = "Remove the object store from the specified synced folder")
    private String cleanObjectStore;

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigPath", arity = 1, description = "The path to the application config")
    private String applicationConfigPath;

    @Override
    public int run() {
        if (! this.help.showHelpIfRequested()) {

            if (null != this.cleanAll) {
                this.cleanAll = this.cleanAll.replaceFirst("^~", System.getProperty("user.home"));

                IValidator pathValidator = new PathValidator(this.cleanAll);

                if (! pathValidator.validate()) {
                    System.out.println("Failed to remove all configuration files: the provided synced folder does not exist");
                    return 1;
                }

                this.removeObjectStoreFolder(this.cleanAll);

                try {
                    this.removeConfigurationFolder();
                    return 0;
                } catch (ValidationException e) {
                    Output.println(e.getMessage());
                    return 1;
                }
            }

            if (this.cleanConfig) {
                try {
                    this.removeConfigurationFolder();
                    return 0;
                } catch (ValidationException e) {
                    Output.println(e.getMessage());
                    return 1;
                }
            }

            if (null != this.cleanObjectStore) {
                IValidator pathValidator = new PathValidator(this.cleanObjectStore);

                if (! pathValidator.validate()) {
                    System.out.println("Failed to remove object store files: the provided synced folder does not exist");
                    return 1;
                }

                this.removeObjectStoreFolder(this.cleanObjectStore);
                return 0;
            }

        }

        return 0;
    }

    /**
     * Remove the .sync folder from the reconciled directory
     *
     * @param rootPath The root path to the synced folder
     */
    private void removeObjectStoreFolder(String rootPath) {
        rootPath = FileUtils.resolveUserHome(rootPath);
        Path syncedFolder = Paths.get(rootPath);

        Path objectStoreFolder = syncedFolder.resolve(Config.DEFAULT.getOsFolderName());

        if (objectStoreFolder.toFile().exists()) {
            FileUtils.delete(objectStoreFolder.toFile());
        }
    }

    /**
     * Remove the config folder in the user's home directory
     *
     * @throws ValidationException If the application config path does not exist
     */
    private void removeConfigurationFolder()
            throws ValidationException {
        Path configDir;

        if (null == this.applicationConfigPath) {
            String resolvedFolderPath = FileUtils.resolveUserHome(org.rmatil.sync.client.config.Config.DEFAULT.getConfigFolderPath());
            configDir = Paths.get(resolvedFolderPath);
        } else {
            IValidator validator = new PathValidator(this.applicationConfigPath);

            if (! validator.validate()) {
                throw new ValidationException("Path " + this.applicationConfigPath + " does not exist");
            }

            String resolvedFolderPath = FileUtils.resolveUserHome(this.applicationConfigPath);
            configDir = Paths.get(resolvedFolderPath);
        }

        if (configDir.toFile().exists()) {
            FileUtils.delete(configDir.toFile());
        }
    }
}
