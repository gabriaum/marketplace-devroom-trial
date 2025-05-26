package com.gabriaum.devroom.backend.database.mongodb;

import com.gabriaum.devroom.backend.database.Database;
import com.gabriaum.devroom.backend.database.DatabaseCredential;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
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
    private MongoDatabase database;

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
        client = MongoClients.create(url);
        database = client.getDatabase(extractDatabaseNameFromUrl(url));
    }

    @Override
    public void disconnect() throws Exception {
        if (isConnected()) {
            client.close();
            client = null;
            database = null;
        }
    }

    @Override
    public boolean isConnected() {
        return client != null && database != null;
    }

    // Método auxiliar para extrair o nome do banco da URL
    private String extractDatabaseNameFromUrl(String url) {
        // Remove parâmetros ?retryWrites...
        String base = url.split("\\?")[0];
        // Pega a parte após a última "/"
        String[] parts = base.split("/");
        return parts.length > 0 ? parts[parts.length - 1] : null;
    }
}
