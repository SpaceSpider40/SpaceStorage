package com.space.file;

import com.space.Commands;
import com.space.Errors;
import com.space.exceptions.*;
import com.space.exceptions.FileAlreadyExistsException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.UUID;

public class FileClientHandler extends Thread {
    private static final Logger logger = LogManager.getLogger(FileClientHandler.class);

    private final Socket socket;
    private final BufferedReader bufferedReader;
    private final BufferedWriter bufferedWriter;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    private boolean canRead = true;

    public FileClientHandler(Socket socket) throws
            RuntimeException,
            SocketException {
        this.socket = socket;
        this.socket.setSoTimeout(0);

        try {
            inputStream = this.socket.getInputStream();
            outputStream = this.socket.getOutputStream();

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Could not get i/o stream: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        logger.info("FileHandler started");

        try {
            write(Commands.ESTABLISHED_CONNECTION);

            listen();

            sleep(1000);

        } catch (IOException | InterruptedException e) {
            logger.error("I/O error: {}", e.getMessage());
        }
    }

    private synchronized void listen() throws
            IOException,
            InterruptedException {
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            line = line.strip()
                    .replace(".", "");

            logger.info("received: {}", line);
            Commands command = Commands.fromString(line);

            switch (command) {
                case Commands.MODAT -> {
                    try {
                        handleModat(UUID.fromString(read()), read());
                    } catch (IllegalArgumentException | UnexpectedCommandException e) {
                        write(Commands.ERROR, Errors.BAD_COMMAND_PARAMS);
                    }
                }
//                case Commands.VAULT_CREATE -> {
//                    try {
//                        write(handleVaultCreate(read()).toString());
//                    } catch (IOException e) {
//                        write(Commands.ERROR, Errors.VAULT_NOT_CREATED);
//                    } catch (VaultAlreadyExistsException e) {
//                        write(Commands.ERROR, Errors.VAULT_ALREADY_EXISTS);
//                    } catch (IllegalArgumentException | UnexpectedCommandException e) {
//                        write(Commands.ERROR, Errors.BAD_COMMAND_PARAMS);
//                    }
//                }
                case Commands.FILE -> {
                    try{

                        handleFile();

                    } catch (IOException e) {
                        write(Commands.ERROR, Errors.FILE_NOT_TRANSFERRED);
                    }catch (VaultNotFoundException e){
                        write(Commands.ERROR, Errors.VAULT_NOT_FOUND);
                    }catch (IllegalArgumentException | UnexpectedCommandException e){
                        write(Commands.ERROR, Errors.BAD_COMMAND_PARAMS);
                    } catch (OutdatedClientFileException e){
                        write(Commands.ERROR, Errors.FILE_OUTDATED);
                    } catch (FileAlreadyExistsException e) {
                        write(Commands.ERROR, Errors.FILE_UP_TO_DATE);
                    }
                }
                default -> {
                    write(Commands.ERROR, Errors.UNKNOWN_COMMAND);
                    logger.warn("Unknown command: {}", line);
                }
            }
        }
    }

    private void handleFile() throws
            IOException,
            VaultNotFoundException,
            IllegalArgumentException,
            UnexpectedCommandException,
            OutdatedClientFileException,
            FileAlreadyExistsException
    {
        UUID uuid = UUID.fromString(read());
        String filepath = read();
        long clientModified = Long.parseLong(read());
        long fileSize = Long.parseLong(read());

        if (!FileManager.getInstance().checkIfVaultExists(uuid)) {
            throw new VaultNotFoundException("Vault: " + uuid + " does not exists");
        }

        if(!FileHelper.validateFilepath(filepath)){
            throw new IllegalArgumentException("Invalid filepath: " + filepath);
        }

        long serverModified = -1;
        try{
            serverModified = FileManager.getInstance().getModificationDate(uuid, filepath);
        } catch (NoSuchFileException _){}

        if (serverModified > clientModified) {
            throw new OutdatedClientFileException("Server contains newer file: " + filepath);
        };

        if (serverModified == clientModified) {
            throw new FileAlreadyExistsException("File: " + filepath + " is up to date");
        }

        //validation done, receive a file
        File preparedFile = FileManager.getInstance().tryCreateFile(uuid, filepath);
        if (preparedFile == null) {
            logger.error("Could not create file: {}", filepath);
            throw new FileSystemException("Could not create file: " + filepath);
        }

        write(Commands.OK);

        try(OutputStream fileOutputStream = Files.newOutputStream(preparedFile.toPath(), StandardOpenOption.WRITE)){
            while (fileSize > 0){
                int toRead = (int) Math.min(fileSize, 1024);
                logger.info("to write: {}", fileSize);
                fileOutputStream.write(inputStream.readNBytes(toRead));
                fileOutputStream.flush();
                fileSize -= 1024;
            }
            logger.info("File: {} received", filepath);
        }

        write(Commands.OK);
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

        logger.info("Writing message: {}", message);

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

    /// Reads single line, validated and strips from unwanted characters.
    /// Expects line to be a parameter
    /// @throws UnexpectedCommandException if read line was a command
    private synchronized String read() throws IOException, UnexpectedCommandException {
        String line = bufferedReader.readLine();

        if (line == null) {
            return "";
        }

        line = line.strip();

        if (line.contains("___")) throw new UnexpectedCommandException();

        return line;
    }

    /// @param commandExpected should expect parameter or command form client
    /// @return striped line
    /// @throws UnexpectedParameterException if it was expecting command but got parameter
    /// @throws UnexpectedCommandException if it was expecting parameter but got command
    private synchronized String read(boolean commandExpected) throws IOException, UnexpectedParameterException, UnexpectedCommandException {
        String line = bufferedReader.readLine();

        if (line == null) {
            return "";
        }

        line = line.strip();

        if (commandExpected){
            if (!line.contains("___")) throw new UnexpectedParameterException();
        }else{
            if (line.contains("___")) throw new UnexpectedCommandException();
        }

        return line;
    }
}
