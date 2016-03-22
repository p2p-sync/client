package org.rmatil.sync.client.command.connect;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.config.Config;
import org.rmatil.sync.client.console.Console;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.util.FileUtils;
import org.rmatil.sync.client.validator.DirectoryValidator;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.exception.InitializationStartException;
import org.rmatil.sync.core.model.ApplicationConfig;
import org.rmatil.sync.core.model.RemoteClientLocation;
import org.rmatil.sync.network.core.model.NodeLocation;
import org.rmatil.sync.persistence.core.tree.local.LocalStorageAdapter;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "connect", description = "Start and connect this device to the p2p network")
public class ConnectCommand implements ICliRunnable {

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
    private HelpOption<ConnectCommand> help;

    @Option(name = {"--bootstrap-ip"}, title = "BootstrapIp", arity = 1, description = "The ip address to which to bootstrap to")
    private String ipAddress;

    @Option(name = {"--bootstrap-port"}, title = "BootstrapPort", arity = 1, description = "The port to which to bootstrap to")
    private Integer port;

    @Option(name = {"-p", "--path"}, title = "SyncFolderPath", arity = 1, description = "The path to the sync folder")
    @Required
    private String syncFolder;

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigFolderPath", arity = 1, description = "The path to the application config folder")
    private String applicationConfigPath;

    @Override
    public int run() {

        if (! this.help.showHelpIfRequested()) {
            try {
                Path configFile;
                if (null == this.applicationConfigPath) {
                    // use the default location for the application config
                    String resolvedFolderPath = FileUtils.resolveUserHome(Config.DEFAULT.getConfigFolderPath());
                    Path configDir = Paths.get(resolvedFolderPath);
                    configFile = configDir.resolve(Config.DEFAULT.getConfigFileName());

                    if (! configFile.toFile().exists()) {
                        Output.println("Default application configuration path " + configFile + " does not exist. Did you initialise the application yet?");
                        return 1;
                    }
                } else {
                    IValidator validator = new PathValidator(this.applicationConfigPath);

                    if (! validator.validate()) {
                        Output.println("Path " + this.applicationConfigPath + " to Application Config does not exist");
                        return 1;
                    }

                    IValidator directoryValidator = new DirectoryValidator(this.applicationConfigPath);

                    if (! directoryValidator.validate()) {
                        Output.println("Path " + this.applicationConfigPath + " should point to the application configuration folder instead of the configuration file");
                        return 1;
                    }

                    configFile = Paths.get(this.applicationConfigPath).resolve(Config.DEFAULT.getConfigFileName());
                }

                byte[] content = Files.readAllBytes(configFile);
                String json = new String(content, StandardCharsets.UTF_8);

                ApplicationConfig appConfig = ApplicationConfig.fromJson(json);

                if (null == appConfig.getPublicKey()) {
                    Output.println("Public key must be set to connect. You can generate a new key pair in the init command");
                    return 1;
                }

                if (null == appConfig.getPrivateKey()) {
                    Output.println("Private key must be set to connect. You can generate a new key pair in the init command");
                    return 1;
                }

                // if an ip address is specified -> use it
                // and check whether a port is specified, if not, use default port
                if (null != this.ipAddress || null != this.port) {
                    if (null != this.ipAddress) {
                        // use default port as fallback
                        int port = org.rmatil.sync.core.config.Config.DEFAULT.getDefaultPort();

                        if (null != appConfig.getBootstrapLocation()) {
                            port = appConfig.getBootstrapLocation().getPort();
                        }

                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        this.ipAddress,
                                        port
                                )
                        );
                    }

                    if (null != this.port && null != appConfig.getBootstrapLocation() &&
                            null != appConfig.getBootstrapLocation().getIpAddress()) {
                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        appConfig.getBootstrapLocation().getIpAddress(),
                                        this.port
                                )
                        );
                    }

                    Output.println("Using configured bootstrap address " + appConfig.getBootstrapLocation().getIpAddress() + ":" + appConfig.getBootstrapLocation().getPort());
                } else if (null != appConfig.getBootstrapLocation() && 0 < appConfig.getBootstrapLocation().getPort() && null != appConfig.getBootstrapLocation().getIpAddress()) {
                    Output.println("Using default bootstrap address " + appConfig.getBootstrapLocation().getIpAddress() + ":" + appConfig.getBootstrapLocation().getPort());
                } else {
                    Output.println("No bootstrap address configured. Starting as bootstrap peer");

                    IValidator validator = new PathValidator(this.syncFolder);
                    if (! validator.validate()) {
                        Output.println("The provided sync folder does not exist");
                        return 1;
                    }
                }

                // ignore a bootstrap port if specified
                Sync sync = new Sync(new LocalStorageAdapter(Paths.get(this.syncFolder)));
                NodeLocation nodeLocation = sync.connect(appConfig);

                Output.println(
                        "Started client " +
                                nodeLocation.getClientDeviceId() +
                                " successfully on " +
                                nodeLocation.getPeerAddress().inetAddress().getHostAddress() +
                                ":" +
                                nodeLocation.getPeerAddress().tcpPort()
                );

                // also register a shutdown hook to correctly terminate Sync
                Runtime.getRuntime().addShutdownHook(new Thread() {
                    @Override
                    public void run() {
                        // shut down correctly
                        Output.print("Shutting down. Please wait... ");
                        sync.shutdown();
                        Output.println("Complete");
                    }
                });

                Console console = new Console(sync);
                console.run();

                sync.shutdown();

            } catch (IOException | InitializationStartException e) {
                Output.println("Could not read the application configuration. Did you initialise the app first?");
                return 1;
            }
        }

        return 0;
    }

}
