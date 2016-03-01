package org.rmatil.sync.client.command.config;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.init.ApplicationConfig;

import javax.inject.Inject;
import java.io.IOException;
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

    @Option(name = {"--publickey"}, title = "PublicKeyPath", description = "The path to the public key to use for the client")
    private boolean publicKeyPath;

    @Option(name = {"--privatekey"}, title = "PrivateKeyPath", description = "The path to the private key to use for the client")
    private boolean privateKeyPath;

    @Option(name = {"--bootstrap-ip"}, title = "BootstrapIp", description = "The ip address of another online client to which this device should bootstrap on start up")
    private boolean bootstrapIp;

    @Option(name = {"--ipv6"}, title = "IpV6", description = "Whether the IP address is an IPv6 address")
    private boolean isIpV6;

    @Option(name = {"--bootstrap-port"}, title = "BootstrapPort", description = "The port of the other client to which this device should bootstrap on start up")
    private boolean bootstrapPort;

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigPath", arity = 1, description = "The path to the application config")
    private String applicationConfigPath;

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {

            try {
                ApplicationConfig appConfig;
                if (null == this.applicationConfigPath) {
                    appConfig = Sync.getApplicationConfig();
                } else {
                    IValidator validator = new PathValidator(this.applicationConfigPath);

                    if (! validator.validate()) {
                        Output.println("Path " + this.applicationConfigPath + " does not exist");
                        return 1;
                    }

                    appConfig = Sync.getApplicationConfig(Paths.get(this.applicationConfigPath));
                }

                if (this.username) {
                    System.out.println("Username: " + appConfig.getUserName());
                }

                if (this.password) {
                    System.out.println("Password: " + appConfig.getPassword());
                }

                if (this.salt) {
                    System.out.println("Salt: " + appConfig.getSalt());
                }

                if (this.cacheTtl) {
                    System.out.println("CacheTtl: " + appConfig.getCacheTtl() + " ms");
                }

                if (this.peerDiscoveryTimeout) {
                    System.out.println("Peer Discovery Timeout: " + appConfig.getPeerDiscoveryTimeout() + " ms");
                }

                if (this.peerBootstrapTimeout) {
                    System.out.println("Peer Bootstrap Timeout: " + appConfig.getPeerBootstrapTimeout() + " ms");
                }

                if (this.shutdownAnnounceTimeout) {
                    System.out.println("Peer Shutdown Timeout: " + appConfig.getShutdownAnnounceTimeout() + " ms");
                }

                if (this.defaultPort) {
                    System.out.println("Default Port: " + appConfig.getDefaultPort());
                }

                if (this.publicKeyPath) {
                    System.out.println("Public Key Path: " + appConfig.getPublicKeyPath());
                }

                if (this.privateKeyPath) {
                    System.out.println("Private Key Path: " + appConfig.getPrivateKeyPath());
                }

                if (this.bootstrapIp) {
                    if (null != appConfig.getDefaultBootstrapLocation()) {
                        System.out.println("Bootstrap Location: " + appConfig.getDefaultBootstrapLocation().getIpAddress());
                    } else {
                        System.out.println("Bootstrap Location: null");
                    }
                }

                if (this.isIpV6) {
                    if (null != appConfig.getDefaultBootstrapLocation()) {
                        System.out.println("IsIPv6: " + appConfig.getDefaultBootstrapLocation().isIpV6());
                    } else {
                        System.out.println("IsIPv6: false");
                    }
                }

                if (this.bootstrapPort) {
                    if (null != appConfig.getDefaultBootstrapLocation()) {
                        System.out.println("Bootstrap Port: " + appConfig.getDefaultBootstrapLocation().getPort());
                    } else {
                        System.out.println("Bootstrap Port: null");
                    }
                }

            } catch (IOException e) {
                System.out.println("Failed to load the application config: " + e.getMessage());
                return 1;
            }

        }

        return 0;
    }
}