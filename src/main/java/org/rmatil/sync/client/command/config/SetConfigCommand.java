package org.rmatil.sync.client.command.config;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.util.FileUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.config.Config;
import org.rmatil.sync.core.model.ApplicationConfig;
import org.rmatil.sync.core.model.RemoteClientLocation;
import org.rmatil.sync.core.security.KeyPairUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

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
                Path configFile;
                if (null == this.applicationConfigPath) {
                    // use the default location for the application config
                    String resolvedFolderPath = FileUtils.resolveUserHome(org.rmatil.sync.client.config.Config.DEFAULT.getConfigFolderPath());
                    Path configDir = Paths.get(resolvedFolderPath);
                    configFile = configDir.resolve(org.rmatil.sync.client.config.Config.DEFAULT.getConfigFileName());

                    if (! configFile.toFile().exists()) {
                        Output.println("Default application configuration path " + configFile + " does not exist. Did you initialise the application yet?");
                        return 1;
                    }
                } else {
                    IValidator validator = new PathValidator(this.applicationConfigPath);

                    if (! validator.validate()) {
                        Output.println("Path to Application Config " + this.applicationConfigPath + " does not exist");
                        return 1;
                    }

                    configFile = Paths.get(this.applicationConfigPath);
                }

                byte[] content = Files.readAllBytes(configFile);
                String json = new String(content, StandardCharsets.UTF_8);

                ApplicationConfig appConfig = ApplicationConfig.fromJson(json);

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
                    appConfig.setPort(this.defaultPort);
                }

                if (null != this.publicKeyPath) {
                    IValidator pathValidator = new PathValidator(this.publicKeyPath);

                    if (! pathValidator.validate()) {
                        Output.println("Path " + this.publicKeyPath + " to public key does not exist");
                        return 1;
                    }

                    Path keyPath = Paths.get(this.publicKeyPath);
                    byte[] publicKeyContent = Files.readAllBytes(keyPath);

                    RSAPublicKey publicKey;
                    try {
                        publicKey = KeyPairUtils.publicKeyFromBytes(publicKeyContent);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        Output.println("Failed to use the given data as RSA public key: " + e.getMessage());
                        return 1;
                    }

                    appConfig.setPublicKey(publicKey);
                }

                if (null != this.privateKeyPath) {
                    IValidator pathValidator = new PathValidator(this.privateKeyPath);

                    if (! pathValidator.validate()) {
                        Output.println("Path " + this.privateKeyPath + " to private key does not exist");
                        return 1;
                    }

                    Path keyPath = Paths.get(this.privateKeyPath);
                    byte[] privateKeyContent = Files.readAllBytes(keyPath);

                    RSAPrivateKey privateKey;
                    try {
                        privateKey = KeyPairUtils.privateKeyFromBytes(privateKeyContent);
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        Output.println("Failed to use the given data as RSA private key: " + e.getMessage());
                        return 1;
                    }

                    appConfig.setPrivateKey(privateKey);
                }

                if (null != this.bootstrapIp) {
                    RemoteClientLocation remoteClientLocation = appConfig.getBootstrapLocation();

                    if (null != remoteClientLocation) {
                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
                                        remoteClientLocation.getPort()
                                )
                        );
                    } else {
                        if (null == this.bootstrapPort) {
                            System.out.println("Assuming default port " + Config.DEFAULT.getDefaultPort());
                        }

                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
                                        Config.DEFAULT.getDefaultPort()
                                )
                        );
                    }
                }

                if (null != this.bootstrapPort) {
                    RemoteClientLocation remoteClientLocation = appConfig.getBootstrapLocation();

                    if (null != remoteClientLocation) {

                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        remoteClientLocation.getIpAddress(),
                                        this.bootstrapPort
                                )
                        );
                    } else {
                        if (null == this.bootstrapIp) {
                            System.out.println("A bootstrap address is also required");
                            return 1;
                        }

                        appConfig.setBootstrapLocation(
                                new RemoteClientLocation(
                                        this.bootstrapIp,
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

                    appConfig.setPublicKey((RSAPublicKey) keyPair.getPublic());
                    appConfig.setPrivateKey((RSAPrivateKey) keyPair.getPrivate());
                }

                // persist all changes
                Files.write(configFile, appConfig.toJson().getBytes(StandardCharsets.UTF_8));

            } catch (IOException e) {
                System.out.println("Failed to load the application config: " + e.getMessage());
                return 1;
            }

        }

        return 0;
    }
}
