package com.gabriaum.devroom.backend.data;

import com.gabriaum.devroom.product.Product;

import java.util.Collection;

public interface ProductData {
    void register(Product product);
    void unregister(Product product);
    Product getProduct(String id);
    boolean hasProduct(String id);
    Collection<Product> getProducts();
}