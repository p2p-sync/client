package org.rmatil.sync.client.command.config;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.security.KeyPairUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.config.Config;
import org.rmatil.sync.core.model.ApplicationConfig;
import org.rmatil.sync.core.model.RemoteClientLocation;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Command(name = "set-config", description = "Set application wide configuration values")
public class SetConfigCommand implements ICliRunnable {

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
    private HelpOption<SetConfigCommand> help;

    @Option(name = {"-u", "--username"}, title = "Username", arity = 1, description = "The username of the user")
    private String username;

    @Option(name = {"-p", "--password"}, title = "Password", arity = 1, description = "The password of the user")
    private String password;

    @Option(name = {"-t", "--salt"}, title = "Salt", arity = 1, description = "The salt of the user's password")
    private String salt;

    @Option(name = {"-c", "--cache-ttl"}, title = "CacheTtl", arity = 1, description = "The time to live for elements in the DHT cache (in milliseconds)")
    private Long cacheTtl;

    @Option(name = {"-d", "--peer-discovery-timeout"}, title = "PeerDiscoveryTimeout", arity = 1, description = "The maximum time to wait until a peer should be discovered (in milliseconds)")
    private Long peerDiscoveryTimeout;

    @Option(name = {"-b", "--peer-bootstrap-timeout"}, title = "PeerBootstrapTimeout", arity = 1, description = "The maximum time to wait until this peer should have been bootstrapped to the remote peer (in milliseconds)")
    private Long peerBootstrapTimeout;

    @Option(name = {"-s", "--peer-shutdown-timeout"}, title = "PeerShutdownTimeout", arity = 1, description = "The maximum time to wait until this peer has successfully announced his friendly shutdown to neighbour peers (in milliseconds)")
    private Long shutdownAnnounceTimeout;

    @Option(name = {"-n", "--port"}, title = "Port", arity = 1, description = "The default port to use for setting up this client")
    private Integer defaultPort;

    @Option(name = {"--publickey"}, title = "PublicKeyPath", arity = 1, description = "The path to the public key to use for the client")
    private String publicKeyPath;

    @Option(name = {"--privatekey"}, title = "PrivateKeyPath", arity = 1, description = "The path to the private key to use for the client")
    private String privateKeyPath;

    @Option(name = {"--bootstrap-ip"}, title = "BootstrapIP", arity = 1, description = "The ip address of another online client to which this device should bootstrap on start up")
    private String bootstrapIp;

    @Option(name = {"--ipv6"}, title = "IpV6", description = "Whether the IP address is an IPv6 address")
    private boolean isIpV6;

    @Option(name = {"--bootstrap-port"}, title = "BootstrapPort", arity = 1, description = "The port of the other client to which this device should bootstrap on start up")
    private Integer bootstrapPort;

    @Option(name = {"--generate-keypair"}, title = "GenerateKeyPair", description = "Generate a keypair and store them in the config folder")
    private boolean generateKeyPair;

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

                if (null != this.username) {
                    appConfig.setUserName(this.username);
                }

                if (null != this.password) {
                    appConfig.setPassword(this.password);
                }

                if (null != this.salt) {
                    appConfig.setSalt(this.salt);
                }

                if (null != this.cacheTtl) {
                    appConfig.setCacheTtl(this.cacheTtl);
                }

                if (null != this.peerDiscoveryTimeout) {
                    appConfig.setPeerDiscoveryTimeout(this.peerDiscoveryTimeout);
                }

                if (null != this.peerBootstrapTimeout) {
                    appConfig.setPeerBootstrapTimeout(this.peerBootstrapTimeout);
                }

                if (null != this.shutdownAnnounceTimeout) {
                    appConfig.setShutdownAnnounceTimeout(this.shutdownAnnounceTimeout);
                }

                if (null != this.defaultPort) {
                    appConfig.setDefaultPort(this.defaultPort);
                }

                if (null != this.publicKeyPath) {
                    appConfig.setPublicKeyPath(this.publicKeyPath);
                }

                if (null != this.privateKeyPath) {
                    appConfig.setPrivateKeyPath(this.privateKeyPath);
                }

                if (null != this.bootstrapIp) {
                    if (null != appConfig.getDefaultBootstrapLocation()) {
                        RemoteClientLocation remoteClientLocation = appConfig.getDefaultBootstrapLocation();

                        appConfig.setDefaultBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
                                        this.isIpV6,
                                        remoteClientLocation.getPort()
                                )
                        );
                    } else {
                        if (null == this.bootstrapPort) {
                            System.out.println("A bootstrap port is also required");
                            return 1;
                        }

                        appConfig.setDefaultBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
                                        this.isIpV6,
                                        this.bootstrapPort
                                )
                        );
                    }
                }

                if (null != this.bootstrapPort) {
                    if (null != appConfig.getDefaultBootstrapLocation()) {
                        RemoteClientLocation remoteClientLocation = appConfig.getDefaultBootstrapLocation();

                        appConfig.setDefaultBootstrapLocation(
                                new RemoteClientLocation(
                                        remoteClientLocation.getIpAddress(),
                                        this.isIpV6,
                                        this.bootstrapPort
                                )
                        );
                    } else {
                        if (null == this.bootstrapIp) {
                            System.out.println("A bootstrap address is also required");
                            return 1;
                        }

                        appConfig.setDefaultBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
                                        this.isIpV6,
                                        this.bootstrapPort
                                )
                        );
                    }
                }

                if (this.generateKeyPair) {
                    KeyPairGenerator keyPairGenerator;
                    try {
                        keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                    } catch (NoSuchAlgorithmException e) {
                        System.out.println("Failed to create key pair: " + e.getMessage());
                        return 1;
                    }

                    KeyPair keyPair = keyPairGenerator.generateKeyPair();

                    String absoluteConfigFolderPath;
                    if (null == this.applicationConfigPath) {
                        absoluteConfigFolderPath = Config.DEFAULT.getConfigFolderPath().replaceFirst("^~", System.getProperty("user.home"));
                    } else {
                        absoluteConfigFolderPath = this.applicationConfigPath;
                    }

                    Path publicKeyPath = Paths.get(absoluteConfigFolderPath).resolve(Config.DEFAULT.getPublicKeyFileName());
                    Path privateKeyPath = Paths.get(absoluteConfigFolderPath).resolve(Config.DEFAULT.getPrivateKeyFileName());

                    KeyPairUtils.writePublicKey((RSAPublicKey) keyPair.getPublic(), publicKeyPath.toString());
                    KeyPairUtils.writePrivateKey((RSAPrivateKey) keyPair.getPrivate(), privateKeyPath.toString());


                    appConfig.setPublicKeyPath(publicKeyPath.toString());
                    appConfig.setPrivateKeyPath(privateKeyPath.toString());
                }

                if (null == this.applicationConfigPath) {
                    Sync.writeApplicationConfig(appConfig);
                } else {
                    Sync.writeApplicationConfig(appConfig, Paths.get(this.applicationConfigPath));
                }

            } catch (IOException e) {
                System.out.println("Failed to load the application config: " + e.getMessage());
                return 1;
            }

        }

        return 0;
    }
}
