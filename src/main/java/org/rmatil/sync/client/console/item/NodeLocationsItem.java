package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.network.api.INodeManager;
import org.rmatil.sync.network.core.model.NodeLocation;
import org.rmatil.sync.persistence.exceptions.InputOutputException;

import java.util.List;

public class NodeLocationsItem implements IItem {

    protected INodeManager nodeManager;
    protected String       username;

    public NodeLocationsItem(INodeManager nodeManager, String username) {
        this.nodeManager = nodeManager;
        this.username = username;
    }

    @Override
    public void execute() {
        try {
            List<NodeLocation> nodeLocations = this.nodeManager.getNodeLocations(this.username);

            Output.println("Current online locations:");
            Output.printNodeLocations(nodeLocations);

        } catch (InputOutputException e) {
            Output.println("Failed to fetch node locations: " + e.getMessage() + ". Please try again");
        }
    }

    @Override
    public String getName() {
        return "Node Locations";
    }

    @Override
    public String getDescription() {
        return "Fetch locations from all currently connected nodes of this user";
    }
}
