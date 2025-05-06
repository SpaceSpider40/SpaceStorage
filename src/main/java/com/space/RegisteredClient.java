package com.space;

import lombok.Getter;

import java.util.UUID;

@Getter
public class RegisteredClient {
    final UUID uid;
    final String path;
    final short port;

    public RegisteredClient(UUID uid, String path, short port) {
        this.uid = uid;
        this.path = path;
        this.port = port;
    }
}
