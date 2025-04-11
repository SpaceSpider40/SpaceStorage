package com.space;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileHandler extends Thread {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final DataOutputStream dataOutputStream;
    private final DataInputStream dataInputStream;

    private boolean canRead = true;

    public FileHandler(Socket socket) throws RuntimeException {
        this.socket = socket;

        try {
            inputStream = this.socket.getInputStream();
            outputStream = this.socket.getOutputStream();

            dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
            dataOutputStream = new DataOutputStream(new BufferedOutputStream(outputStream));
        } catch (IOException e) {
            throw new RuntimeException("Could not get i/o stream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("FileHandler started");

        try {
            write(Commands.ESTABLISHED_CONNECTION.toString());

            listen();

            sleep(1000);

        } catch (IOException | InterruptedException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    private void listen() throws IOException, InterruptedException {
        while (canRead) {
            String line = dataInputStream.readUTF().strip();

            System.out.println("Received Line: " + line.strip());
            System.out.flush();

//            Thread.yield();
//            write(line);
            if (line.equals(Commands.MODAT.toString())) write(Commands.OK.toString());
            sleep(500);
        }
    }

    private long handleModat(String filePath) {
        return System.currentTimeMillis();
    }

    private synchronized void write(String message) throws IOException {

        if (socket.isClosed()) {
            return;
        }

        System.out.println("Writing message: " + message);

        dataOutputStream.writeUTF(message);
        dataOutputStream.flush();
    }
}
