package com.gabriaum.devroom.backend.database;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DatabaseCredential {
    private final String hostName, userName, password, database;
    private final int port;
}