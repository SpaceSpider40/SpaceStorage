package com.space.file;

import java.util.UUID;

public class Vault {
    private final UUID ownerUuid;

    public Vault(UUID ownerUuid) {
        this.ownerUuid = ownerUuid;
    }
}
