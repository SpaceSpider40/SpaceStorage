package com.space;

public enum Commands
{
    ESTABLISHED_CONNECTION("___ESTABLISHED_CONNECTION___\n"),
    MODAT("___MODAT___\n"),
    FILE("___FILE___\n"),
    OK("___OK___\n"),
    UNKNOWN("___UNKNOWN___\n");

    private final String command;
    Commands(final String s) {
        command = s;
    }

    @Override
    public String toString() {
        return command;
    }
}
