package com.space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

public class FileServer extends Thread {
    private ServerSocket serverSocket;

    private final short port;
    private final UUID clientId;

    private boolean isAuth = false;

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
            while (true) {
                Socket socket = serverSocket.accept();
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
                System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                System.out.println(in.readLine());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
