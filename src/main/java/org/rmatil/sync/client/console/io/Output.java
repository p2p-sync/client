package org.rmatil.sync.client.console.io;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.network.core.model.NodeLocation;

import java.security.Key;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Output {

    /**
     * The maximum gap size
     */
    public static final int MAX_GAP_SIZE = 30;

    // TODO: may be use buffered writer or something to write on the same place in the console

    public static void printItems(List<IItem> items) {
        for (int i = 0; i < items.size(); i++) {
            print("[" + i + "]\t" + items.get(i).getName());

            // write spaces until correctly aligned
            for (int j = 0; j < Math.max(0, MAX_GAP_SIZE - items.get(i).getName().length()); j++) {
                print(" ");
            }

            println(" " + items.get(i).getDescription());
        }
    }

    public static void printNodeLocations(List<NodeLocation> nodeLocations) {
        for (NodeLocation nodeLocation : nodeLocations) {
            print(nodeLocation.getClientDeviceId() + "\t");
            print(nodeLocation.getPort() + "\t");
            print(nodeLocation.getIpAddress());
            newLine();
        }

        newLine();
        newLine();
    }

    public static void printFileId(Map<UUID, String> map) {
        for (Map.Entry<UUID, String> entry : map.entrySet()) {
            print(entry.getKey() + "\t");
            print(entry.getValue());
            newLine();
        }

        newLine();
        newLine();
    }

    public static void printKey(Key key) {
        println("Format:  " + key.getFormat());
        println("Algorithm: " + key.getAlgorithm());
        newLine();
        printByteToHex(key.getEncoded());
    }

    public static void printByteToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }

        println(sb.toString());
    }

    public static void newLine() {
        System.out.println();
    }

    public static void print(String text) {
        System.out.print(text);
    }

    public static void println(String text) {
        System.out.println(text);
    }

    public static void printError(Exception e) {
        if (null != e.getMessage()) {
            Output.println("An error occurred during execution of the command: " + e.getMessage());
        } else {
            Output.println("An error occurred during execution of the command. No exception message provided. See log for more details");
        }
    }
}
