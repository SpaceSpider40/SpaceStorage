package com.space.cli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class Cli extends Thread {

    private static final Logger logger = LogManager.getLogger(Cli.class);

    private final Scanner scanner;

    public Cli() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run(){
        if (System.console() == null) {
            logger.error("Failed to initialize cli");
        };

        System.out.println(welcomeMessage());

        String userInput;
        while ((userInput = scanner.next()) != null) {
            logger.info("User input: {}", userInput);


        }
    }

    private String welcomeMessage() {
        final StringBuilder message = new StringBuilder();

        message.append("Welcome to Space Storage\n");
        message.append("Commands:\n");
        message.append("[0] List vaults");

        return message.toString();
    }
}
