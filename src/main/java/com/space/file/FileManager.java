package com.space.file;

import com.space.config.Config;
import com.space.exceptions.VaultAlreadyExistsException;
import com.space.exceptions.VaultNotFoundException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileManager {
    private static volatile FileManager instance;
    private static final Object mutex = new Object();

    private static final HashMap<UUID, File> vaultsMap = new HashMap<>();

    private FileManager() {
        findVaults();
    }

    public static FileManager getInstance() {
        FileManager result = instance;
        if (result == null) {
            synchronized (mutex) {
                result = instance;
                if (result == null) {
                    instance = result = new FileManager();
                }
            }
        }
        return result;
    }

    public UUID CreateVault(String name) throws
            IOException, VaultAlreadyExistsException {
        UUID uuid = createVaultUUID();

        Config.addVault(name, uuid);

        File dir = Files.createDirectories(Path.of(Config.fileSystemRoot, uuid.toString())).toFile();

        vaultsMap.put(uuid, dir);

        return uuid;
    }

    public Long getModificationDate(UUID uuid, String filepath) throws
            IOException,
            VaultNotFoundException
    {

        File v = vaultsMap.get(uuid);

        if (v == null) {
            throw new VaultNotFoundException("Vault " + uuid + " not found");
        }

        System.out.println(v.getAbsoluteFile() + filepath);

        return Files.getLastModifiedTime(Path.of(v.getAbsolutePath(), filepath))
                    .toMillis();
    }

    private UUID createVaultUUID() {
        UUID uuid = UUID.randomUUID();

        //in case we have duplicate
        while (vaultsMap.containsKey(uuid)) {
            uuid = UUID.randomUUID();
        }

        return uuid;
    }

    private void findVaults() {
        File rootDir = new File(Config.fileSystemRoot);

        File[] files = rootDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file == null) continue;
                if (!file.isDirectory()) continue;

                try {
                    UUID uuid = UUID.fromString(file.getName());
                    vaultsMap.put(uuid, file);
                } catch (IllegalArgumentException e) {
                    System.out.println("Invalid UUID: " + file.getName());
                }
            }
        } else {
            System.out.println("No Vault found");
        }

    }
}
