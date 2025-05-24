package com.gabriaum.devroom.backend.data.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.backend.data.AccountData;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.checkerframework.checker.units.qual.A;

import java.util.UUID;

public class AccountDataImpl implements AccountData {
    private final MongoCollection<Document> collection;

    public AccountDataImpl(MongoConnection connection) {
        this.collection = connection.getDatabase().getCollection("accounts");
    }

    @Override
    public synchronized Account register(UUID uniqueId, String name) {
        Account account = new Account(uniqueId, name);
        Document document = new Document("uniqueId", uniqueId.toString())
                .append("name", name);
        collection.insertOne(document);
        return account;
    }

    @Override
    public synchronized Account getAccount(UUID uniqueId) {
        Document document = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        Account account = null;
        if (document != null)
            account = MarketMain.GSON.fromJson(document.toJson(), Account.class);

        return account;
    }

    @Override
    public synchronized Account getAccount(String name) {
        Document document = collection.find(new Document("name", name)).first();
        Account account = null;
        if (document != null)
            account = MarketMain.GSON.fromJson(document.toJson(), Account.class);

        return account;
    }

    @Override
    public synchronized void update(Account account, String fieldName, Object value) {

    }
}
