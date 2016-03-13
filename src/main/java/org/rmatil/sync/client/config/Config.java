package org.rmatil.sync.client.config;

public enum Config {
    DEFAULT("~/.syncconfig", "config");

    private String configFolderPath;

    private String configFileName;

    Config(String configFolderPath, String configFileName) {
        this.configFolderPath = configFolderPath;
        this.configFileName = configFileName;
    }

    public String getConfigFolderPath() {
        return configFolderPath;
    }

    public String getConfigFileName() {
        return configFileName;
    }
}
