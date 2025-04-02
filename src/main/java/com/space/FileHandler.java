package com.space;

import java.net.Socket;

public class FileHandler extends Thread {
    private final Socket socket;

    public FileHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("FileHandler started");
    }
}
