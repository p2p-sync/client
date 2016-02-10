package org.rmatil.sync.client.console.menu;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.ItemStatus;
import org.rmatil.sync.client.console.ItemStatusHolder;
import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.console.item.ExitItem;
import org.rmatil.sync.client.console.item.ShareItem;
import org.rmatil.sync.client.console.item.UnshareItem;
import org.rmatil.sync.core.Sync;

import java.util.ArrayList;
import java.util.List;

public class SharingMenu implements IMenu {

    protected Sync sync;

    protected ItemStatusHolder itemStatus;

    protected List<IItem> menuItems;

    public SharingMenu(Sync sync) {
        this.sync = sync;
        this.itemStatus = new ItemStatusHolder(ItemStatus.RUNNING);
        this.menuItems = new ArrayList<>();
        this.menuItems.add(new ShareItem(this.sync));
        this.menuItems.add(new UnshareItem());
        this.menuItems.add(new ExitItem(this.itemStatus));
    }

    @Override
    public void execute() {

        while (ItemStatus.RUNNING == this.itemStatus.getItemStatus()) {
            // print
            Output.printItems(this.menuItems);

            //scan input
            int selection = Input.getNextInt();

            // invoke correct item
            if (selection >= 0 && selection <= this.menuItems.size() - 1) {
                this.menuItems.get(selection).execute();
            } else {
                Output.println("Invalid selection. Try again");
            }
        }
    }

    @Override
    public String getName() {
        return "Menu Share";
    }

    @Override
    public String getDescription() {
        return "Provide options/share or unshare files or directory";
    }
}
