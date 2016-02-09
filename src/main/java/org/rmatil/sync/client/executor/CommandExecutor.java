package org.rmatil.sync.client.executor;

import com.github.rvesse.airline.Cli;
import com.github.rvesse.airline.SingleCommand;
import com.github.rvesse.airline.parser.errors.ParseException;
import org.rmatil.sync.client.command.ICliRunnable;

public class CommandExecutor {

    private static <T extends ICliRunnable> void execute(T cmd) {
        try {
            int exitCode = cmd.run();
            System.out.println();
            System.out.println("Exiting with Code " + exitCode);
            System.exit(exitCode);
        } catch (Throwable e) {
            System.err.println("Command threw error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static <T extends ICliRunnable> void executeSingleCommand(Class<T> cls, String[] args) {
        SingleCommand<T> parser = SingleCommand.singleCommand(cls);
        try {
            T cmd = parser.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("SingleCommand Parser error: " + e.getMessage());
        } catch (Throwable e) {
            System.err.println("SingleCommand Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public static <T extends ICliRunnable> void executeCli(Cli<T> cli, String[] args) {
        try {
            T cmd = cli.parse(args);
            execute(cmd);
        } catch (ParseException e) {
            System.err.println("Cli Parser error: " + e.getMessage());
        } catch (Throwable e) {
            System.err.println("Cli Unexpected error: " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
