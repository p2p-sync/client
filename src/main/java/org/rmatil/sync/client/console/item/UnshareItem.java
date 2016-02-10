package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;

public class UnshareItem implements IItem {

    @Override
    public void execute() {
        // TODO: unshare
    }

    @Override
    public String getName() {
        return "Unshare";
    }

    @Override
    public String getDescription() {
        return "Unshare a file or directory";
    }
}
