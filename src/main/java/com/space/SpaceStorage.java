package com.space;

import com.space.config.Config;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpaceStorage
{
    private static final Logger logger = LogManager.getLogger(SpaceStorage.class);
    private static final List<FileServer> fileServers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Config.readConfig();

            registerServers();

            startServers();

        } catch (IOException e) {
            logger.log(Level.FATAL, "Server failed to start", e);
        }
    }

    private static void registerServers(){
        Config.registeredClients.forEach(c -> fileServers.add(new FileServer(
                c.port,
                c.uid
        )));
    }

    private static void startServers(){
        fileServers.forEach(fileServer -> {
            logger.log(Level.INFO, "Starting fileserver for: #{}", fileServer.getClientId());
            fileServer.start();
        });
    }
}
