package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;

public class PermissionWriteItem implements IItem {

    @Override
    public void execute() {
        // Nothing to do here
    }

    @Override
    public String getName() {
        return "Read-Write";
    }

    @Override
    public String getDescription() {
        return "Allow the user to read and write to the shared path";
    }
}
