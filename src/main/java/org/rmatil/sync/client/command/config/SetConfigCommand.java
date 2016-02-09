package org.rmatil.sync.client.command.config;

import com.github.rvesse.airline.HelpOption;
import com.github.rvesse.airline.annotations.Command;
import com.github.rvesse.airline.annotations.Option;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.executor.CommandExecutor;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.init.ApplicationConfig;
import org.rmatil.sync.core.model.RemoteClientLocation;

import javax.inject.Inject;
import java.io.IOException;

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

    @Option(name = {"-s", "--salt"}, title = "Salt", arity = 1, description = "The salt of the user's password")
    private String salt;

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


    public static void main(String[] args) {
        CommandExecutor.executeSingleCommand(SetConfigCommand.class, args);
    }

    @Override
    public int run() {
        if (! help.showHelpIfRequested()) {

            try {
                ApplicationConfig appConfig = Sync.getApplicationConfig();

                if (null != this.username) {
                    appConfig.setUserName(this.username);
                }

                if (null != this.password) {
                    appConfig.setPassword(this.password);
                }

                if (null != this.salt) {
                    appConfig.setSalt(this.salt);
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

                Sync.writeApplicationConfig(appConfig);

            } catch (IOException e) {
                System.out.println("Failed to load the application config: " + e.getMessage());
                return 1;
            }

        }

        return 0;
    }
}
