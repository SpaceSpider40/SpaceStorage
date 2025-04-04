package com.space;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class FileHandler extends Thread {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private boolean canRead = true;

    public FileHandler(Socket socket) throws RuntimeException {
        this.socket = socket;

        try {
            inputStream = this.socket.getInputStream();
            outputStream = this.socket.getOutputStream();
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

        } catch (IOException e) {
            System.out.println("I/O error: " + e);
        }
    }

    private void listen() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        while (canRead) {
            String line = br.readLine();

            if (line == null) continue;

            System.out.println("receivedLine: " + line);

            Commands cmd;
            try{
                cmd = Commands.valueOf(line + "\n");
            }catch (Exception e){
                cmd = Commands.UNKNOWN;
            }

            switch (cmd) {
                case Commands.MODAT:
                    String nextLine = br.readLine();

                    System.out.println("nextLine:" +nextLine);

                    long timestamp = handleModat(nextLine);

                    write(timestamp + "\n");
                    break;
                default:
                    write(Commands.UNKNOWN.toString());
                    break;
            }
        }

    }

    private long handleModat(String filePath) {
        return System.currentTimeMillis();
    }

    private void write(String message) throws IOException {

        if (socket.isClosed()) {
            return;
        }

        if (message.charAt(message.length() - 1) != '\n') {
            message += '\n';
        }

        System.out.println("Writing message: " + message);

        outputStream.write(message.getBytes());
    }
}
