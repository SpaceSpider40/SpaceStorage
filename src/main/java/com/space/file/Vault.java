package com.space.file;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class Vault {
    public static final String configFileSuffix = "e9d55cd1-9fdb-4c83-90f5-4db5cf5c6e8c";

    @Getter
    private final Long id;
    private final UUID ownerUuid;
    private final List<UUID> shardClient;
    private final File configFile;
    @Getter
    private final String rootPath;

    private Vault(Long id, UUID ownerUuid, List<UUID> shardClient, File configFile, String rootPath) {
        this.id = id;
        this.ownerUuid = ownerUuid;
        this.shardClient = shardClient;
        this.configFile = configFile;
        this.rootPath = rootPath;
    }

    public static Vault fromConfig(File configFile) throws IOException {
        try (FileReader fr = new FileReader(configFile)) {
            JsonObject obj = JsonParser.parseReader(fr)
                                       .getAsJsonObject();

            Long id = obj.get("id").getAsLong();

            String uuidOwnerStr = obj.get("owner")
                                     .getAsString();
            List<UUID> shardClient = obj.get("shared")
                                        .getAsJsonArray()
                                        .asList()
                                        .stream()
                                        .map(jsonElement -> UUID.fromString(jsonElement.getAsString()))
                                        .collect(Collectors.toList());

            return new Vault(
                    id,
                    UUID.fromString(uuidOwnerStr),
                    shardClient,
                    configFile,
                    configFile.getParent()
            );
        }
    }
}
