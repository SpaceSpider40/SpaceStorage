package com.space;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ERRORS {

    //COMMUNICATION ERRORS
    BAD_COMMAND_PARAMS(301),

    //MISSING ELEMENTS
    VAULT_NOT_FOUND(401),
    ;

    @Getter
    private final long value;
}
