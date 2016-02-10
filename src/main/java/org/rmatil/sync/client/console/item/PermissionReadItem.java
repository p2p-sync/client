package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;

public class PermissionReadItem implements IItem {

    @Override
    public void execute() {
        // nothing to do here
    }

    @Override
    public String getName() {
        return "Read";
    }

    @Override
    public String getDescription() {
        return "Allow the user only to read the shared path";
    }
}
