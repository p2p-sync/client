package org.rmatil.sync.client.console.io;

import java.util.Scanner;

public class Input {

    private static final Scanner scanner = new Scanner(System.in);

    public static void close() {
        scanner.close();
    }

    // TODO: handle input errors

    public static String getInput() {
        return scanner.next();
    }

    public static int getNextInt() {
        return scanner.nextInt();
    }
}
