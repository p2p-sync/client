package org.rmatil.sync.client.console.io;

import org.rmatil.sync.client.console.IItem;

import java.util.List;

public class Output {

    /**
     * The maximum gap size
     */
    public static final int MAX_GAP_SIZE = 20;

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

    public static void print(String text) {
        System.out.print(text);
    }

    public static void println(String text) {
        System.out.println(text);
    }

}
