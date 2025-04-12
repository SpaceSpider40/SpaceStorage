package com.space;

public enum Commands
{
    ESTABLISHED_CONNECTION("___ESTABLISHED_CONNECTION___"),
    MODAT("___MODAT___"),

    VAULT_CREATE("___VAULT_CREATE___"),

    FILE("___FILE___"),
    OK("___OK___"),
    EMPTY("___EMPTY___"),
    UNKNOWN("___UNKNOWN___");

    private final String command;
    Commands(final String s) {
        command = s;
    }

    public static Commands fromString(final String s) {
        for (Commands c : Commands.values()) {
            if (c.command.equalsIgnoreCase(s)) return c;
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return command;
    }
}
