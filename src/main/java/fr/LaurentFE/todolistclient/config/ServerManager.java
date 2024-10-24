package fr.LaurentFE.todolistclient.config;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ServerManager {
    private static ServerManager instance;
    private ServerConfig serverConfig;

    private ServerManager() {

    }

    public static ServerManager getInstance() {
        if (instance == null) {
            instance = new ServerManager();
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

    public static String escapeLabelForAPI(String label) {
        return URLEncoder.encode(label, StandardCharsets.UTF_8)
                .replace("+", "%20")
                .replace("%21", "!")
                .replace("%27", "'")
                .replace("%28", "(")
                .replace("%29", ")")
                .replace("%7E", "~");
    }

    public static void sendPostRequest(String endpoint) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .POST(HttpRequest.BodyPublishers.ofString(""))
                    .build();
            httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendGetRequest(String endpoint) {
        try(HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (InterruptedException | URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void sendPutRequest(String endpoint) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            HttpRequest postRequest = HttpRequest.newBuilder()
                    .uri(new URI(endpoint))
                    .PUT(HttpRequest.BodyPublishers.ofString(""))
                    .build();
            httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
