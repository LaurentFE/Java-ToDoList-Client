package fr.LaurentFE.todolistclient.config;

public class ServerConfig {
    private String server_url;

    public ServerConfig(String serverURI) {
        this.server_url = serverURI;
    }

    public String getServer_url() {
        return server_url;
    }

    public void setServer_url(String server_url) {
        this.server_url = server_url;
    }
}
