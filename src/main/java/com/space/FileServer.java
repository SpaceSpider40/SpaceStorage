package com.space;

import com.space.file.FileClientHandler;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class FileServer extends Thread {
    private static final Logger logger = LogManager.getLogger(FileServer.class);
    private static final long MAX_WAIT_TIME = 3000;
    private static final Thread.Builder virtualThreadBuilder = Thread.ofVirtual().name("fileServerWorker-", 0);

    private ServerSocket serverSocket;

    private final short port;
    @Getter
    private final UUID clientId;

    private final List<FileClientHandler> fileHandlers = new ArrayList<>();

    public FileServer(short port, UUID clientId) {
        super("fileServer-"+port);

        this.port = port;
        this.clientId = clientId;
    }

    @Override
    public void run() {
        super.run();

        try {
            serverSocket = new ServerSocket(port);
            logger.info("Server started on port {}", port);
            while (isAlive()) handle(serverSocket.accept());

        } catch (Throwable e) {
            logger.error("Server(#{}) encountered an error: {}", clientId, e.getMessage());
        }
    }

    private void handle(Socket socket) throws IOException {
        logger.info("Accepted connection from {}", socket.getRemoteSocketAddress());

        socket.setSoTimeout(5000);

        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        virtualThreadBuilder.start(()->{
            try {
                if (auth(in.readLine().strip())){
                    FileClientHandler fh = new FileClientHandler(socket);
                    fh.start();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean auth(String line){
        return Objects.equals(line.trim(), clientId.toString());
    }
}
