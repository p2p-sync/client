package org.rmatil.sync.client;


import com.github.rvesse.airline.builder.CliBuilder;
import org.rmatil.sync.client.command.ICliRunnable;
import org.rmatil.sync.client.command.clean.CleanCommand;
import org.rmatil.sync.client.command.config.GetConfigCommand;
import org.rmatil.sync.client.command.config.SetConfigCommand;
import org.rmatil.sync.client.command.connect.ConnectCommand;
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
                .withCommand(ConnectCommand.class)
                .withDefaultCommand(Help.class);


        CommandExecutor.executeCli(builder.build(), args);
    }
}
