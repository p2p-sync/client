package org.rmatil.sync.client.console.menu;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.ItemStatus;
import org.rmatil.sync.client.console.ItemStatusHolder;
import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.client.console.item.ExitItem;
import org.rmatil.sync.client.console.item.FileIdItem;
import org.rmatil.sync.client.console.item.KeyItem;
import org.rmatil.sync.client.console.item.NodeLocationsItem;
import org.rmatil.sync.core.Sync;

import java.util.ArrayList;
import java.util.List;

public class DhtMenu implements IMenu {

    protected Sync sync;

    protected ItemStatusHolder itemStatus;

    protected List<IItem> menuItems;

    public DhtMenu(Sync sync) {
        this.sync = sync;
        this.itemStatus = new ItemStatusHolder(ItemStatus.RUNNING);
        this.menuItems = new ArrayList<>();
        this.menuItems.add(new NodeLocationsItem(this.sync.getNodeManager(), this.sync.getNode().getUser().getUserName()));
        this.menuItems.add(new FileIdItem(this.sync.getNode().getIdentifierManager()));
        this.menuItems.add(new KeyItem(this.sync.getNodeManager(), this.sync.getNode().getUser()));
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
                Output.println("Invalid selection. Try again ");
            }
        }

        // reset the status to running for next execution
        this.itemStatus.setItemStatus(ItemStatus.RUNNING);
    }

    @Override
    public String getName() {
        return "Menu DHT";
    }

    @Override
    public String getDescription() {
        return "Provide access to values stored in the DHT";
    }
}
