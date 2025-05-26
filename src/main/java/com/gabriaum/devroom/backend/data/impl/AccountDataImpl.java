package com.gabriaum.devroom.backend.data.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.backend.data.AccountData;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.gabriaum.devroom.util.json.JsonUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountDataImpl implements AccountData {
    private final MongoCollection<Document> collection;

    public AccountDataImpl(MongoConnection connection) {
        MongoDatabase database = connection.getDatabase();
        List<String> collections = database.listCollectionNames().into(new ArrayList<>());
        if (!collections.contains("accounts"))
            database.createCollection("accounts");

        this.collection = database.getCollection("accounts");
    }

    @Override
    public synchronized Account register(UUID uniqueId, String name) {
        Account account = new Account(uniqueId, name);
        Document document = Document.parse(MarketMain.GSON.toJson(account));
        collection.insertOne(document);
        MarketMain.getInstance().getLogger().info("Account registered for player: " + name);
        return account;
    }

    @Override
    public synchronized Account getAccount(UUID uniqueId) {
        Document document = collection.find(new Document("uniqueId", uniqueId.toString())).first();
        if (document != null) {
            return MarketMain.GSON.fromJson(document.toJson(), Account.class);
        }
        return null;
    }

    @Override
    public synchronized Account getAccount(String name) {
        Document document = collection.find(new Document("name", name)).first();
        if (document != null) {
            return MarketMain.GSON.fromJson(document.toJson(), Account.class);
        }
        return null;
    }

    @Override
    public synchronized void update(Account account, String fieldName) {
        JsonObject tree = MarketMain.GSON.toJsonTree(account).getAsJsonObject();
        Document doc = collection.find(Filters.eq("uniqueId", account.getUniqueId().toString())).first();
        if (doc != null) {
            JsonElement value = null;
            if (tree.has(fieldName))
                value = tree.get(fieldName);

            Document updateDoc = new Document();
            if (value != null) {
                Object bsonValue = JsonUtils.convertToBson(value);
                updateDoc.append("$set", new Document(fieldName, bsonValue));
            } else updateDoc.append("$unset", new Document(fieldName, ""));

            collection.updateOne(doc, updateDoc);
        }
    }
}
