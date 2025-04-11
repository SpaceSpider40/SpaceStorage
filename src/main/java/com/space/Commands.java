package com.space;

public enum Commands
{
    ESTABLISHED_CONNECTION("___ESTABLISHED_CONNECTION___"),
    MODAT("___MODAT___"),
    FILE("___FILE___"),
    OK("___OK___"),
    EMPTY("___EMPTY___"),
    UNKNOWN("___UNKNOWN___");

    private final String command;
    Commands(final String s) {
        command = s;
    }

    @Override
    public String toString() {
        return command;
    }
}
