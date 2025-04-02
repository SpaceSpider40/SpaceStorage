package com.space;

import java.util.UUID;

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
