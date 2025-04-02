package com.space;

import com.space.config.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpaceStorage
{

    private static final List<FileServer> fileServers = new ArrayList<>();

    public static void main(String[] args) {
        try {
            Config.readConfig();

            registerServers();

            startServers();

        } catch (IOException e) {
            System.err.println("Could not read config file: " + e.getMessage());
        }
    }

    private static void registerServers(){
        Config.registeredClients.forEach(c -> fileServers.add(new FileServer(
                c.port,
                c.uid
        )));
    }

    private static void startServers(){
        fileServers.forEach(FileServer::start);
    }
}
