package com.space.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.space.RegisteredClient;
import com.space.exceptions.VaultAlreadyExistsException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Config {
    public static String fileSystemRoot;
    public static final List<RegisteredClient> registeredClients = new ArrayList<>();

    public static void readConfig() throws IOException {

        //Read main config
        try (FileReader fr = new FileReader("config.json")) {
            JsonObject obj = JsonParser.parseReader(fr).getAsJsonObject();

            parseClients(obj.get("clients").getAsJsonArray());
            parseServer(obj.get("server").getAsJsonObject());
        }
    }

    public static HashMap<String, UUID> readVaults() throws IOException {
        HashMap<String, UUID> vaults = new HashMap<>();

        try (FileReader fr = new FileReader("vaults.json")) {
            JsonArray objArr = JsonParser.parseReader(fr).getAsJsonArray();
            objArr.forEach(obj -> {
                JsonObject jo = obj.getAsJsonObject();

                vaults.put(
                        jo.get("name").getAsString(),
                        UUID.fromString(jo.get("uuid").getAsString())
                );
            });
        }

        return vaults;
    }

    public static void addVault(String vaultName, UUID uuid) throws IOException, VaultAlreadyExistsException {
        HashMap<String, UUID> vaults = readVaults();

        if (vaults.containsKey(vaultName)) {
            throw new VaultAlreadyExistsException("Vault " + vaultName + " already exists");
        }

        vaults.put(vaultName, uuid);

        try (FileWriter fw = new FileWriter("vaults.json")) {
            JsonArray vaultArr = new JsonArray();

            for (String name : vaults.keySet()) {
                JsonObject jo = new JsonObject();
                jo.addProperty("name", name);
                jo.addProperty("uuid", vaults.get(name).toString());

                vaultArr.add(jo);
            }

            fw.write(vaultArr.toString());
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
