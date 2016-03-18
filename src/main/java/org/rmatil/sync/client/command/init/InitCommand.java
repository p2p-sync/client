package org.rmatil.sync.client.command.init;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.config.Config;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.util.FileUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.init.ApplicationConfigFactory;
import org.rmatil.sync.core.model.ApplicationConfig;
import org.rmatil.sync.persistence.core.tree.ITreeStorageAdapter;
import org.rmatil.sync.persistence.core.tree.local.LocalStorageAdapter;
import org.rmatil.sync.persistence.exceptions.InputOutputException;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

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
                ApplicationConfig appConfig = ApplicationConfigFactory.createDefaultApplicationConfig();

                if (null == this.applicationConfigPath) {
                    String resolvedFolderPath = FileUtils.resolveUserHome(Config.DEFAULT.getConfigFolderPath());
                    configDir = Paths.get(resolvedFolderPath);
                } else {
                    IValidator validator = new PathValidator(this.applicationConfigPath);

                    if (! validator.validate()) {
                        Output.println("Path " + this.applicationConfigPath + " does not exist");
                        return 1;
                    }

                    String resolvedFolderPath = FileUtils.resolveUserHome(this.applicationConfigPath);
                    configDir = Paths.get(resolvedFolderPath);
                }

                // create configuration directory
                if (! configDir.toFile().exists()) {
                    Files.createDirectories(configDir);
                }

                // create config file
                Path configFile = configDir.resolve(Config.DEFAULT.getConfigFileName());
                if (! configFile.toFile().exists()) {
                    Files.createFile(configFile);
                } else {
                    // if the config already exists, we transfer the existing
                    // public private key pair
                    byte[] content = Files.readAllBytes(configFile);
                    String json = new String(content, StandardCharsets.UTF_8);

                    ApplicationConfig oldConfig = ApplicationConfig.fromJson(json);

                    appConfig.setPublicKey(oldConfig.getPublicKey());
                    appConfig.setPrivateKey(oldConfig.getPrivateKey());
                }

                Files.write(configFile, appConfig.toJson().getBytes(StandardCharsets.UTF_8));

                if (null != this.syncFolder) {
                    IValidator validator = new PathValidator(this.syncFolder);

                    if (! validator.validate()) {
                        Output.println("Path " + syncFolderPath + " does not exist");
                        return 1;
                    }

                    ITreeStorageAdapter treeStorageAdapter = new LocalStorageAdapter(syncFolderPath);
                    try {
                        Sync.init(treeStorageAdapter);
                    } catch (InputOutputException e) {
                        Output.println("Could not initialise sync at directory " + syncFolderPath + ": " + e.getMessage());
                        return 1;
                    }

                    Output.println("Initialized sync directory at " + syncFolderPath);
                    Output.println("Configuration directory is at " + configDir);
                    Output.println("Configuration file is at " + configFile);
                }
            } catch (IOException | NoSuchAlgorithmException e) {
                Output.println("Failed to initialise sync folder: " + e.getMessage());
                return 1;
            }
        }

        return 0;
    }
}
