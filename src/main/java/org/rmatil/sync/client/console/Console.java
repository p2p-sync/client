package org.rmatil.sync.client.console;

import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.console.item.ExitItem;
import org.rmatil.sync.client.console.menu.SharingMenu;
import org.rmatil.sync.core.Sync;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class Console {

    protected Sync sync;

    protected ItemStatusHolder itemStatus;

    protected List<IItem> items;

    public Console(Sync sync) {
        this.sync = sync;
        this.itemStatus = new ItemStatusHolder(ItemStatus.RUNNING);
        this.items = new ArrayList<>();
        this.items.add(new SharingMenu(this.sync));
        this.items.add(new ExitItem(this.itemStatus));
    }

    public void run() {
        while (ItemStatus.RUNNING == this.itemStatus.getItemStatus()) {

            Output.newLine();
            Output.println("Select the action to invoke:");

            // print items
            Output.printItems(this.items);

            // scan input
            int selection;
            try {
                selection = Input.getNextInt();
            } catch (InputMismatchException e) {
                Output.println("Invalid input. Try again");
                continue;
            }

            // invoke correct item
            if (selection >= 0 && selection <= this.items.size() - 1) {
                try {
                    this.items.get(selection).execute();
                } catch (Exception e) {
                    // catch all exceptions here to avoid printing
                    // an ugly stack trace on the command
                    Output.printError(e);
                }
            } else {
                Output.println("Invalid selection. Try again");
            }
        }

        Input.close();
    }
}
