package org.rmatil.sync.client.console;

public class ItemStatusHolder {

    protected ItemStatus itemStatus;

    public ItemStatusHolder(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }

    public ItemStatus getItemStatus() {
        return itemStatus;
    }

    public void setItemStatus(ItemStatus itemStatus) {
        this.itemStatus = itemStatus;
    }
}
