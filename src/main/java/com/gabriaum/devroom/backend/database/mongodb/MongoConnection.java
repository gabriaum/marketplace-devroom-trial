package com.gabriaum.devroom.backend.database.mongodb;

import com.gabriaum.devroom.backend.database.Database;
import com.gabriaum.devroom.backend.database.DatabaseCredential;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

import java.util.Objects;
import java.util.regex.Pattern;

@Getter
public class MongoConnection implements Database {
    private static final String PATTERN = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";
    private static final Pattern IP_PATTERN = Pattern.compile(PATTERN + "\\." + PATTERN + "\\." + PATTERN + "\\." + PATTERN);

    private final String url;
    private MongoClient client;
    private com.mongodb.client.MongoDatabase database;

    public MongoConnection(DatabaseCredential credential) {
        this.url = IP_PATTERN.matcher(credential.getHostName()).matches()
                ? "mongodb://" + (credential.getUserName().isEmpty() ? "" : credential.getUserName() + ":" + credential.getPassword() + "@")
                + credential.getHostName() + "/" + credential.getDatabase()
                + "?retryWrites=true&w=majority"
                : "mongodb+srv://" + (credential.getUserName().isEmpty() ? "" : credential.getUserName() + ":" + credential.getPassword() + "@")
                + credential.getHostName() + "/"
                + credential.getDatabase() + "?retryWrites=true&w=majority";
    }

    @Override
    public void connect() throws Exception {
        MongoClientURI uri = new MongoClientURI(url);
        client = new MongoClient(uri);
        database = client.getDatabase(Objects.requireNonNull(uri.getDatabase()));
    }

    @Override
    public void disconnect() throws Exception {
        if (isConnected())
            client.close();
    }

    @Override
    public boolean isConnected() {
        return client != null && database != null;
    }
}
