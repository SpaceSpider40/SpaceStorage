package com.space;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Errors {

    //COMMUNICATION ERRORS
    BAD_COMMAND_PARAMS(301),

    //VAULT ERRORS
    VAULT_NOT_FOUND(401),
    VAULT_NOT_CREATED(402),
    VAULT_ALREADY_EXISTS(403),
    ;

    @Getter
    private final long value;
}
