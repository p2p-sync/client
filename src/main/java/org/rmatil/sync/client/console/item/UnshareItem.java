package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Input;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.core.Sync;
import org.rmatil.sync.core.exception.SharingFailedException;
import org.rmatil.sync.core.syncer.sharing.event.UnshareEvent;
import org.rmatil.sync.persistence.api.IPathElement;
import org.rmatil.sync.persistence.api.StorageType;
import org.rmatil.sync.persistence.core.tree.ITreeStorageAdapter;
import org.rmatil.sync.persistence.core.tree.TreePathElement;
import org.rmatil.sync.persistence.exceptions.InputOutputException;
import org.rmatil.sync.version.api.AccessType;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UnshareItem implements IItem {

    protected Sync sync;

    protected List<IItem> items;

    public UnshareItem(Sync sync) {
        this.sync = sync;
        this.items = new ArrayList<>();
        this.items.add(new PermissionReadItem());
        this.items.add(new PermissionWriteItem());
    }

    @Override
    public void execute() {
        boolean unshared = false;
        while (! unshared) {

            Output.println("Type the relative path within the synced folder to the file which should be unshared");
            String inputPath = Input.getInput();

            ITreeStorageAdapter storageAdapter = this.sync.getStorageAdapter();

            Path rootPath = Paths.get(storageAdapter.getRootDir().getPath());
            Path pathToUnshare = rootPath.resolve(inputPath);

            TreePathElement elementToUnshare = new TreePathElement(pathToUnshare.toString());

            try {
                if (! storageAdapter.exists(StorageType.DIRECTORY, elementToUnshare) &&
                        ! storageAdapter.exists(StorageType.FILE, elementToUnshare)) {

                    Output.println("The provided path does not exist. Please try again");
                    continue;
                }
            } catch (InputOutputException e) {
                Output.println("Could not check whether the element on path " + elementToUnshare.getPath() + " exists. Skipping...");
                continue;
            }

            Output.println("Type the name of the user to unshare with");
            String username = Input.getInput();

            try {
                if (! this.sync.getNode().getUserManager().isRegistered(username)) {
                    Output.println("No user found for username " + username + ". Unsharing failed");
                    return;
                }
            } catch (InputOutputException e) {
                Output.println("Could not check whether user with username " + username + " exists. Unsharing failed");
                return;
            }

            AccessType accessType = AccessType.READ;
            boolean valid = false;
            while (! valid) {
                Output.println("Select the sharing permissions for the user which should be removed");

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
            Output.println("Removing permissions of " + pathToUnshare + " and all its contents from user " + username + " (Access: " + accessType + ")");

            UnshareEvent unshareEvent = new UnshareEvent(
                    Paths.get(inputPath),
                    accessType,
                    username
            );

            List<UnshareEvent> pathsToShare = new ArrayList<>();
            pathsToShare.add(unshareEvent);

            try {
                if (storageAdapter.isDir(elementToUnshare)) {
                    List<TreePathElement> pathElements = storageAdapter.getDirectoryContents(elementToUnshare);

                    for (IPathElement child : pathElements) {
                        pathsToShare.add(
                                new UnshareEvent(
                                        Paths.get(child.getPath()),
                                        accessType,
                                        username
                                )
                        );
                    }
                }
            } catch (InputOutputException e) {
                Output.println("Failed to unshare also children: " + e.getMessage() + " Try again");
                continue;
            }

            for (UnshareEvent evenToUnshare : pathsToShare) {
                Output.println("Unsharing " + evenToUnshare.getRelativePath());
                try {
                    this.sync.getSharingSyncer().sync(evenToUnshare);
                } catch (SharingFailedException e) {
                    Output.println("Failed to unshare item " + evenToUnshare.getRelativePath() + ": " + e.getMessage());
                }
            }

            unshared = true;
        }
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
