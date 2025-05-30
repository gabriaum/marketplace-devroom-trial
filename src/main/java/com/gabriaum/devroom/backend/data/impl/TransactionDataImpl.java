package com.gabriaum.devroom.backend.data.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.sold.ProductSold;
import com.gabriaum.devroom.backend.data.TransactionData;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.yaml.snakeyaml.error.Mark;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class TransactionDataImpl implements TransactionData {
    private final MongoCollection<Document> collection;

    public TransactionDataImpl(MongoConnection connection) {
        MongoDatabase database = connection.getDatabase();
        List<String> collections = database.listCollectionNames().into(new ArrayList<>());
        if (!collections.contains("transactions"))
            database.createCollection("transactions");

        this.collection = database.getCollection("transactions");
    }

    @Override
    public void register(ProductSold productSold) {
        Document document = Document.parse(MarketMain.GSON.toJson(productSold));
        collection.insertOne(document);
        MarketMain.sendDebug("Product sold registered: " + productSold.getProduct().getId());
    }

    @Override
    public Collection<ProductSold> getAllSoldProductsBy(UUID uniqueId) {
        List<ProductSold> products = new ArrayList<>();
        for (Document document : collection.find(new Document("purchasedById", uniqueId.toString()))) {
            ProductSold productSold = MarketMain.GSON.fromJson(document.toJson(), ProductSold.class);
            products.add(productSold);
        }

        for (Document document : collection.find(new Document("product.announceById", uniqueId.toString()))) {
            ProductSold productSold = MarketMain.GSON.fromJson(document.toJson(), ProductSold.class);
            if (!products.contains(productSold))
                products.add(productSold);
        }

        return products;
    }

    @Override
    public Collection<ProductSold> getAllSoldProductsBy(String name) {
        List<ProductSold> products = new ArrayList<>();
        for (Document document : collection.find(new Document("purchaseByName", name))) {
            ProductSold productSold = MarketMain.GSON.fromJson(document.toJson(), ProductSold.class);
            products.add(productSold);
        }
        return products;
    }
}
