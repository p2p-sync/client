package org.rmatil.sync.client.command.connect;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import com.github.rvesse.airline.annotations.restrictions.Required;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.console.Console;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.security.KeyPairUtils;
import org.rmatil.sync.client.validator.IValidator;
import org.rmatil.sync.client.validator.PathValidator;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.exception.InitializationStartException;
import org.rmatil.sync.core.init.ApplicationConfig;
import org.rmatil.sync.core.model.RemoteClientLocation;
import org.rmatil.sync.network.core.model.ClientDevice;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

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

    @Option(name = {"-a", "--app-config-path"}, title = "AppConfigPath", arity = 1, description = "The path to the application config")
    private String applicationConfigPath;

    @Override
    public int run() {

        if (! this.help.showHelpIfRequested()) {
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

                String bootstrapAddress = null;
                Integer bootstrapPort = null;

                if (null != appConfig.getDefaultBootstrapLocation()) {
                    bootstrapAddress = appConfig.getDefaultBootstrapLocation().getIpAddress();
                    bootstrapPort = appConfig.getDefaultBootstrapLocation().getPort();
                }

                if (null != this.ipAddress) {
                    bootstrapAddress = this.ipAddress;
                } else if (null != bootstrapAddress) {
                    System.out.println("Using default bootstrap address " + bootstrapAddress);
                } else {
                    System.out.println("No bootstrap address configured. Starting as bootstrap peer");

                    IValidator validator = new PathValidator(this.syncFolder);
                    if (! validator.validate()) {
                        System.out.println("The provided sync folder does not exist");
                        return 1;
                    }

                    PrivateKey privateKey;
                    try {
                        privateKey = KeyPairUtils.readPrivateKey(appConfig.getPrivateKeyPath());
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        System.out.println("Failed to read the private key from its file: " + e.getMessage());
                        return 1;
                    }

                    PublicKey publicKey;
                    try {
                        publicKey = KeyPairUtils.readPublicKey(appConfig.getPublicKeyPath());
                    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                        System.out.println("Failed to read the public key from its file: " + e.getMessage());
                        return 1;
                    }

                    KeyPair keyPair = new KeyPair(publicKey, privateKey);

                    // ignore a bootstrap port if specified
                    Sync sync = new Sync(Paths.get(this.syncFolder));
                    ClientDevice clientDevice = sync.connect(
                            keyPair,
                            appConfig.getUserName(),
                            appConfig.getPassword(),
                            appConfig.getSalt(),
                            appConfig.getCacheTtl(),
                            appConfig.getPeerDiscoveryTimeout(),
                            appConfig.getPeerBootstrapTimeout(),
                            appConfig.getPeerBootstrapTimeout(),
                            appConfig.getDefaultPort(),
                            appConfig.getIgnorePatterns()
                    );

                    System.out.println(
                            "Started client " +
                                    clientDevice.getClientDeviceId() +
                                    " successfully on " +
                                    clientDevice.getPeerAddress().inetAddress().getHostAddress() +
                                    ":" +
                                    clientDevice.getPeerAddress().tcpPort()
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

                    return 0;
                }

                if (null != this.port) {
                    bootstrapPort = this.port;
                } else if (null != bootstrapPort) {
                    System.out.println("Using default bootstrap port " + bootstrapPort);
                } else {
                    System.out.println("No bootstrap port is specified. Can not connect to " + bootstrapAddress);
                    return 1;
                }

                IValidator validator = new PathValidator(this.syncFolder);
                if (! validator.validate()) {
                    System.out.println("The provided sync folder does not exist");
                    return 1;
                }

                PrivateKey privateKey;
                try {
                    privateKey = KeyPairUtils.readPrivateKey(appConfig.getPrivateKeyPath());
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    System.out.println("Failed to read the private key from its file: " + e.getMessage());
                    return 1;
                }

                PublicKey publicKey;
                try {
                    publicKey = KeyPairUtils.readPublicKey(appConfig.getPublicKeyPath());
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    System.out.println("Failed to read the public key from its file: " + e.getMessage());
                    return 1;
                }

                KeyPair keyPair = new KeyPair(publicKey, privateKey);

                // now connect to the specified boostrap peer
                final Sync sync = new Sync(Paths.get(this.syncFolder));

                // TODO: check that all values are correct (at least username, ...)

                ClientDevice clientDevice = sync.connect(
                        keyPair,
                        appConfig.getUserName(),
                        appConfig.getPassword(),
                        appConfig.getSalt(),
                        appConfig.getCacheTtl(),
                        appConfig.getPeerDiscoveryTimeout(),
                        appConfig.getPeerBootstrapTimeout(),
                        appConfig.getPeerBootstrapTimeout(),
                        appConfig.getDefaultPort(),
                        new RemoteClientLocation(
                                bootstrapAddress,
                                appConfig.getDefaultBootstrapLocation().isIpV6(), // TODO: bootstrap locatino may not be set
                                bootstrapPort
                        ),
                        appConfig.getIgnorePatterns()
                );

                System.out.println(
                        "Started client " +
                                clientDevice.getClientDeviceId() +
                                " successfully on " +
                                clientDevice.getPeerAddress().inetAddress().getHostAddress() +
                                ":" +
                                clientDevice.getPeerAddress().tcpPort()
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
                System.out.println("Could not read the application configuration. Did you initialise the app first?");
                return 1;
            }
        }

        return 0;
    }

}
