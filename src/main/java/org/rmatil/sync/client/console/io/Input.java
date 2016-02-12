package org.rmatil.sync.client.console.io;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Input {

    private static final Scanner scanner = new Scanner(System.in);

    public static void close() {
        scanner.close();
    }

    public static String getInput() {
        return scanner.next();
    }

    public static int getNextInt()
            throws InputMismatchException {
        return scanner.nextInt();
    }
}
