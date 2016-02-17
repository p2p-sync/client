package org.rmatil.sync.client.console.item;

import org.rmatil.sync.client.console.IItem;
import org.rmatil.sync.client.console.io.Output;
import org.rmatil.sync.network.api.INodeManager;
import org.rmatil.sync.network.api.IUser;
import org.rmatil.sync.persistence.exceptions.InputOutputException;

import java.security.PrivateKey;
import java.security.PublicKey;

public class KeyItem implements IItem {

    protected INodeManager nodeManager;
    protected IUser        user;

    public KeyItem(INodeManager nodeManager, IUser user) {
        this.nodeManager = nodeManager;
        this.user = user;
    }

    @Override
    public void execute() {
        try {
            PublicKey publicKey = this.nodeManager.getPublicKey(this.user.getUserName());
            PrivateKey privateKey = this.nodeManager.getPrivateKey(this.user);

            Output.println("Public Key");

            if (null != publicKey) {
                Output.printKey(publicKey);
            } else {
                Output.println("null");
            }

            if (null != privateKey) {
                Output.printKey(privateKey);
            } else {
                Output.println("null");
            }

        } catch (InputOutputException e) {
            Output.println("Failed to fetch public/private key: " + e.getMessage() + ". Please try again");
        }
    }

    @Override
    public String getName() {
        return "Get public/private key";
    }

    @Override
    public String getDescription() {
        return "Print the public / private key";
    }
}
