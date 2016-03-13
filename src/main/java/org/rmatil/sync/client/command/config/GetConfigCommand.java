package org.rmatil.sync.client.command.config;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.config.Config;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.util.FileUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.model.ApplicationConfig;
import org.rmatil.sync.core.security.KeyPairUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Command(name = "get-config", description = "Get application wide configuration values")
public class GetConfigCommand implements ICliRunnable {

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
    private HelpOption<GetConfigCommand> help;

    @Option(name = {"-u", "--username"}, title = "Username", description = "The username of the user")
    private boolean username;

    @Option(name = {"-p", "--password"}, title = "Password", description = "The password of the user")
    private boolean password;

    @Option(name = {"-t", "--salt"}, title = "Salt", description = "The salt of the user's password")
    private boolean salt;

    @Option(name = {"-c", "--cache-ttl"}, title = "CacheTtl", description = "The time to live for elements in the DHT cache (in milliseconds)")
    private boolean cacheTtl;

    @Option(name = {"-d", "--peer-discovery-timeout"}, title = "PeerDiscoveryTimeout", description = "The maximum time to wait until a peer should be discovered (in milliseconds)")
    private boolean peerDiscoveryTimeout;

    @Option(name = {"-b", "--peer-bootstrap-timeout"}, title = "PeerBootstrapTimeout", description = "The maximum time to wait until this peer should have been bootstrapped to the remote peer (in milliseconds)")
    private boolean peerBootstrapTimeout;

    @Option(name = {"-s", "--peer-shutdown-timeout"}, title = "PeerShutdownTimeout", description = "The maximum time to wait until this peer has successfully announced his friendly shutdown to neighbour peers (in milliseconds)")
    private boolean shutdownAnnounceTimeout;

    @Option(name = {"-n", "--port"}, title = "Port", description = "The default port to use for setting up this client")
    private boolean defaultPort;

    @Option(name = {"--publickey"}, title = "PublicKey", description = "The public key to use for the client")
    private boolean publicKey;

    @Option(name = {"--privatekey"}, title = "PrivateKey", description = "The private key to use for the client")
    private boolean privateKey;

    @Option(name = {"--bootstrap-ip"}, title = "BootstrapIp", description = "The ip address of another online client to which this device should bootstrap on start up")
    private boolean bootstrapIp;

    @Option(name = {"--bootstrap-port"}, title = "BootstrapPort", description = "The port of the other client to which this device should bootstrap on start up")
    private boolean bootstrapPort;

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigPath", arity = 1, description = "The path to the application config")
    private String applicationConfigPath;

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {

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
                        Output.println("Path " + this.applicationConfigPath + " does not exist");
                        return 1;
                    }

                    configFile = Paths.get(this.applicationConfigPath);
                }

                byte[] content = Files.readAllBytes(configFile);
                String json = new String(content, StandardCharsets.UTF_8);

                ApplicationConfig appConfig = ApplicationConfig.fromJson(json);

                if (this.username) {
                    Output.println("Username: " + appConfig.getUserName());
                }

                if (this.password) {
                    Output.println("Password: " + appConfig.getPassword());
                }

                if (this.salt) {
                    Output.println("Salt: " + appConfig.getSalt());
                }

                if (this.cacheTtl) {
                    Output.println("CacheTtl: " + appConfig.getCacheTtl() + " ms");
                }

                if (this.peerDiscoveryTimeout) {
                    Output.println("Peer Discovery Timeout: " + appConfig.getPeerDiscoveryTimeout() + " ms");
                }

                if (this.peerBootstrapTimeout) {
                    Output.println("Peer Bootstrap Timeout: " + appConfig.getPeerBootstrapTimeout() + " ms");
                }

                if (this.shutdownAnnounceTimeout) {
                    Output.println("Peer Shutdown Timeout: " + appConfig.getShutdownAnnounceTimeout() + " ms");
                }

                if (this.defaultPort) {
                    Output.println("Default Port: " + appConfig.getPort());
                }

                if (this.publicKey) {
                    if (null == appConfig.getPublicKey()) {
                        Output.println("Public Key: null");
                    } else {
                        Output.println("Public Key: " + KeyPairUtils.byteToHexString(appConfig.getPublicKey().getEncoded()));
                    }
                }

                if (this.privateKey) {
                    if (null == appConfig.getPrivateKey()) {
                        Output.println("Private Key: null");
                    } else {
                        Output.println("Private Key: " + KeyPairUtils.byteToHexString(appConfig.getPrivateKey().getEncoded()));
                    }
                }

                if (this.bootstrapIp) {
                    if (null != appConfig.getBootstrapLocation()) {
                        Output.println("Bootstrap Location: " + appConfig.getBootstrapLocation().getIpAddress());
                    } else {
                        Output.println("Bootstrap Location: null");
                    }
                }

                if (this.bootstrapPort) {
                    if (null != appConfig.getBootstrapLocation()) {
                        Output.println("Bootstrap Port: " + appConfig.getBootstrapLocation().getPort());
                    } else {
                        Output.println("Bootstrap Port: null");
                    }
                }

            } catch (IOException e) {
                Output.println("Failed to load the application config: " + e.getMessage());
                return 1;
            }

        }

        return 0;
    }
}
