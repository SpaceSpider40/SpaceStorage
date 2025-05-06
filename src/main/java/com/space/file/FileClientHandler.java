package com.space.file;

import com.space.Commands;
import com.space.ERRORS;
import com.space.exceptions.VaultNotFoundException;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

public class FileClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;

    private boolean canRead = true;

    public FileClientHandler(Socket socket) throws
            RuntimeException,
            SocketException
    {
        this.socket = socket;
        this.socket.setSoTimeout(0);

        try {
            var inputStream  = this.socket.getInputStream();
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
            InterruptedException
    {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.strip()
                       .replace(".", "");

            System.out.println("received: " + line);
            Commands command = Commands.fromString(line);

            switch (command) {
                case Commands.MODAT -> {
                    try {
                        handleModat(Long.parseLong(bufferedReader.readLine()),
                                    bufferedReader.readLine());
                    } catch (NumberFormatException e) {
                        write(Commands.ERROR, ERRORS.BAD_COMMAND_PARAMS);
                    }
                }
                default -> {
                    System.out.println("Unknown command: " + line);
                    //todo: log the incident
                }
            }
        }
    }

    private void handleModat(Long vaultId, String filePath) throws
            IOException
    {
        Long modificationTime;

        try {
            modificationTime = FileManager.getInstance()
                                          .getModificationDate(vaultId, filePath);
        } catch (IOException e) {
            modificationTime = 0L;
        } catch (VaultNotFoundException e) {
            write(Commands.ERROR);
            write(ERRORS.VAULT_NOT_FOUND);
            return;
        }

        write(String.valueOf(modificationTime));
    }

    private synchronized void write(String message) throws
            IOException
    {

        if (socket.isClosed()) {
            return;
        }

        System.out.println("Writing message: " + message);

        bufferedWriter.write(message);
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private synchronized void write(Commands command) throws
            IOException
    {
        write(command.toString());
    }

    private synchronized void write(ERRORS error) throws
            IOException
    {
        write(error.getValue());
    }

    private synchronized void write(Commands command, ERRORS errors) throws
            IOException
    {
        write(command);
        write(errors);
    }

    private synchronized void write(int number) throws
            IOException
    {
        write(String.valueOf(number));
    }

    private synchronized void write(long number) throws
            IOException
    {
        write(String.valueOf(number));
    }
}
