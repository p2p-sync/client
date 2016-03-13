# P2P-Sync Client

P2P-Sync Client is a simple, console based end-user client to create or connect to a file sharing network using 
the [P2P-Sync Library](https://github.com/p2p-sync/sync). It allows to synchronise a specified folder among multiple 
machines in the same network in a completely distributed manner. Furthermore, files and directories within that folder can 
be shared with other users in the same network.

## Install using Maven

To use this module, add the following to your `pom.xml` file:

```xml

<repositories>
  <repository>
    <id>persistence-mvn-repo</id>
    <url>https://raw.github.com/p2p-sync/client/mvn-repo/</url>
    <snapshots>
      <enabled>true</enabled>
      <updatePolicy>always</updatePolicy>
    </snapshots>
  </repository>
</repositories>

<dependencies>
  <dependency>
    <groupId>org.rmatil.sync.client</groupId>
    <artifactId>sync-client</artifactId>
    <version>0.1-SNAPSHOT</version>
  </dependency>
</dependencies>

```

# Overview


## Deployment

To deploy this module to various end user clients, a script named `deploy.sh` is included [here](https://github.com/p2p-sync/client/blob/master/deploy.sh).
Feel free to adjust it to your needs.

**NOTE**: The provided RSA public and private key pair in this repository is only used for test purposes in the local network! Do not use it in your production environment! (Instead generate a new key pair as described [here](https://help.github.com/articles/generating-an-ssh-key/))

## Usage

After building the client using Maven, you will find a ZIP archive in the target directory.
To run the client, `cd` into the `bin` directory. In there, a log file (`sync.log`) and an executable bash script is placed.

### Help

To display a short help message about the available commands, use 

```bash
  ./sync help
```

To get help about a particular command, type 

```bash
  ./sync help <commandName>
```

### Initialise the Client

To create the default application configuration for [P2P-Sync](https://github.com/p2p-sync/sync), run the following command:

```bash
  ./sync init -p <pathToTheSynchronisedFolder>
```

whereas `<pathToTheSynchronsedFolder>` should be replaced with an existing directory on your file system then used as 
synchronised folder. Besides creating the necessary directories for the shared folders, it will create the default 
configuration in your home directory (`~/.syncconfig`).
To specify in which folder the configuration should be placed, add the flag `-a <pathToConfigDir>` to the command above.

### Configuration

After having initialised the default configuration, particular values can be modified by using the command `config`.
Use `./sync help config set-config` to get a list of available config keys to set. 
Setting a new value for `port`, for example, is done by typing:

```bash
  ./sync config set-config --port 4004
```

To get the current value of a setting, use

```bash
  ./sync config get-config --port
```

### Connect

To create a network using the machine on which the command is invoked, run

```bash
  ./sync connect -p <pathToTheSynchronisedFolder>
```

In case no default bootstrap location is configured, the node will start up as bootstrap peer.
Otherwise, it will try to connect to the IP-address-port pair specified in the configuration.

In order to connect to a well-known node which has joined the network previously, type

```bash
  ./sync connect -p <pathToTheSynchronisedFolder> --bootstrap-ip <ipToNode> --bootstrap-port <portNrOfNode>
```

with `<ipToNode>` the ip address (IPv4 or IPv6) and `<portNrOfNode>` the port of the node to connect.
As previously, the amendment of `-a <pathToConfigDir>` specifies the folder in which the application configuration resides.

### Sharing

After the node has been started successfully, a simple console interface allows to share files or directories
within the specified synchronised folder.

### Clean Up

To clean all generated files (especially the configuration folder `~/.syncconfig` and the object store in the synchronised folder), use 

```bash
  ./sync clean --clean-all <pathToTheSynchronisedFolder>
```
