package com.gabriaum.devroom.backend.database;

public interface Database {
    void connect() throws Exception;
    void disconnect() throws Exception;
    boolean isConnected();
}