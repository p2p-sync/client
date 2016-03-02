package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.network.api.IIdentifierManager;
import org.rmatil.sync.persistence.api.IPathElement;
import org.rmatil.sync.persistence.api.IStorageAdapter;
import org.rmatil.sync.persistence.core.local.LocalPathElement;
import org.rmatil.sync.persistence.exceptions.InputOutputException;

import java.util.List;
import java.util.UUID;

public class FileIdItem implements IItem {

    protected IStorageAdapter                  storageAdapter;
    protected IIdentifierManager<String, UUID> identifierManager;

    public FileIdItem(IStorageAdapter storageAdapter, IIdentifierManager<String, UUID> identifierManager) {
        this.storageAdapter = storageAdapter;
        this.identifierManager = identifierManager;
    }

    @Override
    public void execute() {
        try {
            List<IPathElement> entries = this.storageAdapter.getDirectoryContents(new LocalPathElement(""));

            Output.println("Current registered file ids");
            Output.newLine();
            for (IPathElement entry : entries) {
                UUID fileId = this.identifierManager.getValue(entry.getPath());

                Output.print(entry.getPath() + "\t");
                Output.print(fileId.toString());
                Output.newLine();
            }
            
        } catch (InputOutputException e) {
            Output.println("Failed to fetch file ids: " + e.getMessage() + ". Please try again");
        }
    }

    @Override
    public String getName() {
        return "File Ids";
    }

    @Override
    public String getDescription() {
        return "Fetch all file ids known to this user";
    }
}
