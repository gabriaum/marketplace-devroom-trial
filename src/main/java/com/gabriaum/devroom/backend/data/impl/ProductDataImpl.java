package com.gabriaum.devroom.backend.data.impl;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.backend.data.ProductData;
import com.gabriaum.devroom.backend.database.mongodb.MongoConnection;
import com.gabriaum.devroom.product.Product;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ProductDataImpl implements ProductData {
    private final MongoCollection<Document> collection;

    public ProductDataImpl(MongoConnection connection) {
        MongoDatabase database = connection.getDatabase();

        List<String> collections = database.listCollectionNames().into(new ArrayList<>());
        if (!collections.contains("products"))
            database.createCollection("products");

        this.collection = database.getCollection("products");
    }

    @Override
    public void register(Product product) {
        Document document = Document.parse(MarketMain.GSON.toJson(product));
        collection.insertOne(document);
    }

    @Override
    public void unregister(Product product) {
        Document filter = new Document("id", product.getId());
        collection.deleteOne(filter);
    }

    @Override
    public Product getProduct(String id) {
        Document document = collection.find(new Document("id", id)).first();
        if (document != null)
            return MarketMain.GSON.fromJson(document.toJson(), Product.class);
        return null;
    }

    @Override
    public boolean hasProduct(String id) {
        Document document = collection.find(new Document("id", id)).first();
        return document != null;
    }

    @Override
    public Collection<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        for (Document document : collection.find()) {
            Product product = MarketMain.GSON.fromJson(document.toJson(), Product.class);
            products.add(product);
        }

        return products;
    }
}
