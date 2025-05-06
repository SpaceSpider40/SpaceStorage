package com.space;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.UUID;

final public class JsonUtils {
    private JsonUtils() {}


    public static JsonObject createVaultConfig(
            Long id,
            UUID owner,
            UUID[] shared
    ){
        JsonObject config = new JsonObject();
        config.addProperty("id", id);
        config.addProperty("owner", owner.toString());

        JsonArray sharedArray = new JsonArray();
        for(UUID uuid : shared){
            sharedArray.add(uuid.toString());
        }
        config.add("shared", sharedArray);

        return config;
    }
}
