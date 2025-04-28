package com.space.file;

import com.space.config.Config;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FileManager {
    private static volatile FileManager instance;
    private static final Object mutex = new Object();

    private static final List<Vault> registeredVaults = new ArrayList<>();

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

    public Long getModificationDate(Long vaultId, String filepath) throws IOException {



        Path path = Paths.get(filepath);

        return Files.getLastModifiedTime(path)
                    .toMillis();
    }

    private void findVaults() {
        File rootDir = new File(Config.fileSystemRoot);

        File[] files = rootDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file == null) continue;
                if (!file.isDirectory()) continue;

                File[] vaults = file.listFiles();

                if (vaults == null) continue;

                for (File vaultFile : vaults) {
                    if (vaultFile.isFile() && vaultFile.getName()
                                                       .equalsIgnoreCase("vc_" + Vault.configFileSuffix + ".json")) {
                        System.out.println("Found vault " + vaultFile);
                        try {
                            registeredVaults.add(Vault.fromConfig(vaultFile));
                        } catch (IOException e) {
                            System.err.println("Failed to load vault " + vaultFile);
                        }
                    }
                }
            }
        } else {
            System.out.println("No Vault found");
        }

    }
}
