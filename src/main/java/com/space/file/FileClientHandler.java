package com.space.file;

import com.space.Commands;
import com.space.Errors;
import com.space.exceptions.VaultAlreadyExistsException;
import com.space.exceptions.VaultNotFoundException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FileClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    private boolean canRead = true;

    public FileClientHandler(Socket socket) throws
            RuntimeException,
            SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(0);

        try {
            var inputStream = this.socket.getInputStream();
            var outputStream = this.socket.getOutputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Could not get i/o stream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        System.out.println("FileHandler started");

        try {
            write(Commands.ESTABLISHED_CONNECTION);

            listen();

            sleep(1000);

        } catch (IOException | InterruptedException e) {
            System.out.println("I/O error: " + e.getMessage());
        }
    }

    private synchronized void listen() throws
            IOException,
            InterruptedException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.strip()
                    .replace(".", "");

            System.out.println("received: " + line);
            Commands command = Commands.fromString(line);

            switch (command) {
                case Commands.MODAT -> {
                    try {
                        handleModat(UUID.fromString(bufferedReader.readLine()),
                                bufferedReader.readLine());
                    } catch (IllegalArgumentException e) {
                        write(Commands.ERROR, Errors.BAD_COMMAND_PARAMS);
                    }
                }
                case Commands.VAULT_CREATE -> {
                    try {
                        write(handleVaultCreate(bufferedReader.readLine()).toString()); //TODO: fix waiting for infinity if client doesn't send next line or sends command
                    } catch (IOException e) {
                        write(Commands.ERROR, Errors.VAULT_NOT_CREATED);
                    } catch (VaultAlreadyExistsException e) {
                        write(Commands.ERROR, Errors.VAULT_ALREADY_EXISTS);
                    } catch (IllegalArgumentException e) {
                        write(Commands.ERROR, Errors.BAD_COMMAND_PARAMS);
                    }
                }
                default -> {
                    System.out.println("Unknown command: " + line);
                    //todo: log the incident
                }
            }
        }
    }

    private void handleModat(UUID uuid, String filePath) throws
            IOException {
        Long modificationTime;

        try {
            modificationTime = FileManager.getInstance()
                    .getModificationDate(uuid, filePath);
        } catch (IOException e) {
            modificationTime = 0L;
        } catch (VaultNotFoundException e) {
            write(Commands.ERROR);
            write(Errors.VAULT_NOT_FOUND);
            return;
        }

        write(String.valueOf(modificationTime));
    }

    private UUID handleVaultCreate(String name) throws
            IOException, VaultAlreadyExistsException, IllegalArgumentException {

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (name.contains("/")) {
            throw new IllegalArgumentException("Name cannot contain /");
        }

        if (name.contains("___")){
            throw new IllegalArgumentException("Name cannot contain ___");
        }

        return FileManager.getInstance().CreateVault(name);
    }

    private synchronized void write(String message) throws
            IOException {

        if (socket.isClosed()) {
            return;
        }

        System.out.println("Writing message: " + message);

        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private synchronized void write(Commands command) throws
            IOException {
        write(command.toString());
    }

    private synchronized void write(Errors error) throws
            IOException {
        write(error.getValue());
    }

    private synchronized void write(Commands command, Errors errors) throws
            IOException {
        write(command);
        write(errors);
    }

    private synchronized void write(int number) throws
            IOException {
        write(String.valueOf(number));
    }

    private synchronized void write(long number) throws
            IOException {
        write(String.valueOf(number));
    }
}
