package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.network.api.IIdentifierManager;
import org.rmatil.sync.persistence.exceptions.InputOutputException;

import java.util.Map;
import java.util.UUID;

public class FileIdItem implements IItem {

    protected IIdentifierManager<String, UUID> identifierManager;

    public FileIdItem(IIdentifierManager<String, UUID> identifierManager) {
        this.identifierManager = identifierManager;
    }

    @Override
    public void execute() {
        try {
            Map<UUID, String> valueMap = this.identifierManager.getIdentifierMap().getValueMap();

            Output.println("Current registered file ids");
            Output.printFileId(valueMap);

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
