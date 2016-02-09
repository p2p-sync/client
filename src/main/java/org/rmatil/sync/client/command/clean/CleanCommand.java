package org.rmatil.sync.client.command.clean;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.util.FileUtil;
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

    @Option(name = {"-a", "--clean-all"}, title = "CleanAll", arity = 1, description = "Remove the configuration directory as well as the object store from the specified synced folder")
    private String cleanAll;

    @Option(name = {"-c", "--clean"}, title = "CleanConfig", description = "Remove the configuration directory")
    private boolean cleanConfig;

    @Option(name = {"-o", "--clean-os"}, title = "CleanObjectStore", arity = 1, description = "Remove the object store from the specified synced folder")
    private String cleanObjectStore;

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
                this.removeConfigurationFolder();

                return 0;
            }

            if (this.cleanConfig) {
                this.removeConfigurationFolder();
                return 0;
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
        rootPath = rootPath.replaceFirst("^~", System.getProperty("user.home"));
        Path syncedFolder = Paths.get(rootPath);

        Path objectStoreFolder = syncedFolder.resolve(Config.DEFAULT.getOsFolderName());

        if (objectStoreFolder.toFile().exists()) {
            FileUtil.delete(objectStoreFolder.toFile());
        }
    }

    /**
     * Remove the config folder in the user's home directory
     */
    private void removeConfigurationFolder() {
        String configFolderPath = Config.DEFAULT.getConfigFolderPath();
        configFolderPath = configFolderPath.replaceFirst("^~", System.getProperty("user.home"));

        Path configPath = Paths.get(configFolderPath).toAbsolutePath();

        if (configPath.toFile().exists()) {
            FileUtil.delete(configPath.toFile());
        }
    }
}
