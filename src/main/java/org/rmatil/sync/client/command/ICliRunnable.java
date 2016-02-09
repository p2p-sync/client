package org.rmatil.sync.client.command;

public interface ICliRunnable {

    /**
     * Runs the command.
     * Furthermore, it returns an exit code that should be
     * returned by the invoking applications
     *
     * @return The exit code to return
     */
    int run();
}
