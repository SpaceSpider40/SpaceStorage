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

public class FileManager {
    private static volatile FileManager instance;
    private static final Object mutex = new Object();

    private static final List<Vault> registeredVaults = new ArrayList<>();

    private FileManager() {

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

    public Long getModificationDate(String filepath) throws IOException {
        Path path = Paths.get(filepath);

        return Files.getLastModifiedTime(path).toMillis();
    }

    private void findVaults(){
        Path path = Paths.get(Config.fileSystemRoot);

        path.forEach(file -> {
            System.out.println("file: " + file);
        });
    }
}
