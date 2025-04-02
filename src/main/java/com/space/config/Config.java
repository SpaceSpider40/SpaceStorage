package com.space.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.space.RegisteredClient;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class Config {
    public static final List<RegisteredClient> registeredClients = new ArrayList<>();

    public static void readConfig() throws IOException {
        try(FileReader fr = new FileReader("config.json")){
            JsonObject obj = JsonParser.parseReader(fr).getAsJsonObject();

            obj.get("clients").getAsJsonArray().forEach(e -> {
                JsonObject client = e.getAsJsonObject();

                registeredClients.add(new RegisteredClient(
                        UUID.fromString(client.get("uid").getAsString()),
                        client.get("path").getAsString(),
                        client.get("port").getAsShort()
                ));
            });
        }
    }

}
