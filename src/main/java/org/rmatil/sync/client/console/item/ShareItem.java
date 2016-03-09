package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.exception.SharingFailedException;
import org.rmatil.sync.core.syncer.sharing.event.ShareEvent;
import org.rmatil.sync.persistence.api.IPathElement;
import org.rmatil.sync.persistence.core.tree.ITreeStorageAdapter;
import org.rmatil.sync.persistence.core.tree.TreePathElement;
import org.rmatil.sync.persistence.exceptions.InputOutputException;
import org.rmatil.sync.version.api.AccessType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ShareItem implements IItem {

    protected Sync sync;

    protected List<IItem> items;

    public ShareItem(Sync sync) {
        this.sync = sync;
        this.items = new ArrayList<>();
        this.items.add(new PermissionReadItem());
        this.items.add(new PermissionWriteItem());
    }

    @Override
    public void execute() {
        boolean shared = false;
        while (! shared) {

            Output.println("Type the relative path within the synced folder to the file which should be shared");
            String inputPath = Input.getInput();

            Path rootPath = this.sync.getRootPath();
            Path pathToShare = rootPath.resolve(inputPath);

            if (! pathToShare.toFile().exists()) {
                Output.println("The provided path does not exist. Please try again");
                continue;
            }

            Output.println("Type the name of the user to share with");
            String username = Input.getInput();

            // TODO: check whether user exists

            AccessType accessType = AccessType.READ;
            boolean valid = false;
            while (! valid) {
                Output.println("Select the sharing permissions for the user");

                Output.printItems(this.items);

                int selection = Input.getNextInt();
                if (selection >= 0 && selection <= this.items.size() - 1) {
                    if (this.items.get(selection) instanceof PermissionReadItem) {
                        accessType = AccessType.READ;
                        valid = true;
                    } else if (this.items.get(selection) instanceof PermissionWriteItem) {
                        accessType = AccessType.WRITE;
                        valid = true;
                    } else {
                        Output.println("Invalid input. Please try again");
                    }
                } else {
                    Output.println("Invalid input. Please try again");
                }
            }

            // now get all children
            Output.println("Sharing " + inputPath + " and all its contents with user " + username + " (Access: " + accessType + ")");

            ShareEvent shareEvent = new ShareEvent(
                    Paths.get(inputPath),
                    accessType,
                    username
            );

            List<ShareEvent> pathsToShare = new ArrayList<>();
            pathsToShare.add(shareEvent);

            ITreeStorageAdapter storageAdapter = this.sync.getStorageAdapter();
            TreePathElement sharedPathElement = new TreePathElement(pathToShare.toString());
            try {
                if (storageAdapter.isDir(sharedPathElement)) {
                    List<TreePathElement> pathElements = storageAdapter.getDirectoryContents(sharedPathElement);

                    for (IPathElement child : pathElements) {
                        pathsToShare.add(
                                new ShareEvent(
                                        Paths.get(child.getPath()),
                                        accessType,
                                        username
                                )
                        );
                    }
                }
            } catch (InputOutputException e) {
                Output.println("Failed to share also children: " + e.getMessage() + " Try again");
                continue;
            }

            for (ShareEvent evenToShare : pathsToShare) {
                Output.println("Sharing " + evenToShare.getRelativePath());
                try {
                    this.sync.getSharingSyncer().sync(evenToShare);
                } catch (SharingFailedException e) {
                    Output.println("Failed to share item " + evenToShare.getRelativePath() + ": " + e.getMessage());
                }
            }

            shared = true;
        }
    }

    @Override
    public String getName() {
        return "Share";
    }

    @Override
    public String getDescription() {
        return "Share a file or directory";
    }
}
