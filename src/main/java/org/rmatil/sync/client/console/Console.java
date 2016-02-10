package org.rmatil.sync.client.console;

import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.console.item.ExitItem;
import org.rmatil.sync.client.console.menu.SharingMenu;
import org.rmatil.sync.core.Sync;

import java.util.ArrayList;
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

            // TODO: print items
            Output.printItems(this.items);

            // TODO: scan input
            int selection = Input.getNextInt();

            // TODO: invoke correct item
            if (selection >= 0 && selection <= this.items.size() - 1) {
                this.items.get(selection).execute();
            } else {
                Output.println("Invalid selection. Try again");
            }
        }
    }
}
