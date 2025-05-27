package com.gabriaum.devroom.backend.data;

import com.gabriaum.devroom.account.sold.ProductSold;

import java.util.Collection;
import java.util.UUID;

public interface TransactionData {
    void register(ProductSold productSold);
    Collection<ProductSold> getAllSoldProductsBy(UUID uniqueId);
    Collection<ProductSold> getAllSoldProductsBy(String name);
}