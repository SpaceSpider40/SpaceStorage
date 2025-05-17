package com.space.file;

import com.space.config.Config;
import com.space.exceptions.VaultAlreadyExistsException;
import com.space.exceptions.VaultNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class FileManager {
    private static final Logger logger = LogManager.getLogger(FileManager.class);
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

    public File tryCreateFile(UUID vaultId, String filepath) throws IOException {
        File vaultRoot = vaultsMap.get(vaultId);

        if(vaultRoot == null) return null;

        File file = Paths.get(vaultRoot.getAbsolutePath(), filepath).toFile();

        file.getParentFile().mkdirs();

        boolean ignore = file.createNewFile();

        return file;
    }

    public boolean checkIfVaultExists(UUID uuid) {
        return vaultsMap.containsKey(uuid);
    }

    public FileInputStream prepareFile(UUID uuid, String filename) {

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
                    logger.error("Invalid UUID: {}", file.getName());

                }
            }
        } else {
            logger.error("No Vault found");
        }

    }
}
