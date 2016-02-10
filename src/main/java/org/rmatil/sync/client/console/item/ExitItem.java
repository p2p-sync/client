package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.ItemStatus;
import org.rmatil.sync.client.console.ItemStatusHolder;

public class ExitItem implements IItem {

    protected ItemStatusHolder itemStatus;

    public ExitItem(ItemStatusHolder itemStatus) {
        this.itemStatus = itemStatus;
    }

    @Override
    public void execute() {
        this.itemStatus.setItemStatus(ItemStatus.TERMINATED);
    }

    @Override
    public String getName() {
        return "Exit";
    }

    @Override
    public String getDescription() {
        return "Exits the current menu";
    }
}
