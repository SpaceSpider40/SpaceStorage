package com.space.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.space.RegisteredClient;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Config {
    public static String fileSystemRoot;
    public static final List<RegisteredClient> registeredClients = new ArrayList<>();

    public static void readConfig() throws IOException {
        try (FileReader fr = new FileReader("config.json")) {
            JsonObject obj = JsonParser.parseReader(fr).getAsJsonObject();

            parseClients(obj.get("clients").getAsJsonArray());
            parseServer(obj.get("server").getAsJsonObject());
        }
    }

    private static void parseClients(JsonArray clients) {
        clients.getAsJsonArray().forEach(e -> {
            JsonObject client = e.getAsJsonObject();

            registeredClients.add(new RegisteredClient(
                    UUID.fromString(client.get("uid").getAsString()),
                    client.get("path").getAsString(),
                    client.get("port").getAsShort()
            ));
        });
    }

    private static void parseServer(JsonObject server) {
        JsonObject fileManager = server.get("fileManager").getAsJsonObject();

        fileSystemRoot = fileManager.get("root").getAsString();
    }

}
