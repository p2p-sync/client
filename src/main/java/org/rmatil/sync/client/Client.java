package org.rmatil.sync.client;


import com.github.rvesse.airline.builder.CliBuilder;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.command.clean.CleanCommand;
import org.rmatil.sync.client.command.config.GetConfigCommand;
import org.rmatil.sync.client.command.config.SetConfigCommand;
import org.rmatil.sync.client.command.init.InitCommand;
import org.rmatil.sync.client.executor.CommandExecutor;
import org.rmatil.sync.client.help.Help;
import org.rmatil.sync.core.Sync;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {

    public static final String HEADER = "\nP2P-Sync allows you to synchronise and share files among clients of different users\n\n";
    public static final String FOOTER = "\nPlease report issues to http://github.com/p2p-sync/sync/issues\nLicense: Apache License 2.0";

    public static final String HELP_OPTION      = "help";
    public static final String INIT_OPTION      = "init";
    public static final String BOOTSTRAP_OPTION = "bootstrap";
    public static final String CONNECT_OPTION   = "connect";
    public static final String TARGET_OPTION    = "target";

    public static void main(String[] args) {
        CliBuilder<ICliRunnable> builder = com.github.rvesse.airline.Cli.<ICliRunnable>builder("sync")
                // Add a description
                .withDescription("P2P-Sync allows you to synchronise and share files among clients of different users");

        builder
                .withGroup("config")
                .withDescription("Set configuration values")
                .withCommand(SetConfigCommand.class)
                .withCommand(GetConfigCommand.class);

        builder
                .withCommand(Help.class)
                .withCommand(InitCommand.class)
                .withCommand(CleanCommand.class)
                .withDefaultCommand(Help.class);


        CommandExecutor.executeCli(builder.build(), args);


//        Sync sync = new Sync(path);
//        ClientDevice client1 = sync.init(keyPair, "raphael", "password", "salt", 4003, null);
//
//        Sync sync2 = new Sync(path2);
//        sync2.connect(keyPair, "raphael", "password", "salt", 4004, new RemoteClientLocation(
//                client1.getPeerAddress().inetAddress().getHostName(),
//                client1.getPeerAddress().isIPv6(),
//                client1.getPeerAddress().tcpPort()
//        ));
    }

    protected static void initSync(String targetSyncPath)
            throws IllegalArgumentException {

        Path syncFolder = Client.validateSyncFolder(targetSyncPath);

        Sync app = new Sync(syncFolder);
        try {
            Sync.init(syncFolder);
        } catch (IOException e) {
            throw new RuntimeException("An exception occurred during initiating the synced folder settings. See logs for more information. Error: " + e.getMessage());
        }
    }

    protected static void connect() {

    }

    protected static void bootstrap(String targetSyncPath) {
        Path syncFolder = Client.validateSyncFolder(targetSyncPath);

        Sync sync = new Sync(syncFolder);
//        sync.connect()
    }

    protected static Path validateSyncFolder(String targetSyncPath) {
        Path syncFolder = Paths.get(targetSyncPath).toAbsolutePath();

        if (! syncFolder.toFile().exists()) {
            throw new IllegalArgumentException("The given target path " + syncFolder.toString() + " does not exist");
        }

        return syncFolder;
    }
}
