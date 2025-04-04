package com.space;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileServer extends Thread {
    private static final long MAX_WAIT_TIME = 3000;
    private static final Thread.Builder virtualThreadBuilder = Thread.ofVirtual().name("worker-", 0);

    private ServerSocket serverSocket;

    private final short port;
    private final UUID clientId;

    private final List<FileHandler> fileHandlers = new ArrayList<>();

    public FileServer(short port, UUID clientId) {
        this.port = port;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        super.run();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
            while (isAlive()) handle(serverSocket.accept());

        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }
    }

    private void handle(Socket socket) throws IOException {
        System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());

        long maxTimeout = System.currentTimeMillis() + MAX_WAIT_TIME;

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        virtualThreadBuilder.start(() -> {
            while (System.currentTimeMillis() < maxTimeout) {
                String line;
                try {
                    line = in.readLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (line == null) continue;
                if (!auth(line)) continue;

                try{

                    FileHandler fh = new FileHandler(socket);

                    fileHandlers.add(fh);

                    fh.start();

                }catch(Exception e){
                    System.err.println(e.getMessage());
                }

                interrupt();
            }

            if (!isInterrupted()){
                try {
                    socket.close();

                    System.out.println(" timeout for: " + socket.getRemoteSocketAddress());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private boolean auth(String line){
        return Objects.equals(line.trim(), clientId.toString());
    }
}
