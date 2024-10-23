package fr.LaurentFE.todolistclient.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class ConfigurationManager {
    private static ConfigurationManager instance;
    private ServerConfig serverConfig;

    private ConfigurationManager() {

    }

    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    public void loadServerConfig() {
        try {
            File target_server = new File("src/main/resources/target_server.json");
            Scanner myReader = new Scanner(target_server);
            StringBuilder json_file = new StringBuilder();
            while (myReader.hasNextLine()) {
                json_file.append(myReader.nextLine());
            }
            Gson gson = new Gson();
            serverConfig = gson.fromJson(json_file.toString(), ServerConfig.class);
        } catch (IOException e) {
            String error_msg = "target_server.json file not formatted properly";
            throw new RuntimeException(error_msg, e);
        }
    }

    public ServerConfig getServerConfig() {
        if (serverConfig == null) {
            String error_msg = "Trying to read Server Configuration before it was loaded";
            throw new RuntimeException(error_msg);
        }
        return serverConfig;
    }
}
