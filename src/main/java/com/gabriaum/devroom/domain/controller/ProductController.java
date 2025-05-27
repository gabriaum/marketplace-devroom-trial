package com.gabriaum.devroom.domain.controller;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.account.Account;
import com.gabriaum.devroom.product.Product;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class ProductController extends ArrayList<Product> {

    @Override
    public boolean add(Product product) {
        Account account = Account.getAccount(product.getAnnounceById());
        if (account == null) throw new IllegalArgumentException("Account not found for the product announcer ID: " + product.getAnnounceById());

        CompletableFuture.runAsync(() -> MarketMain.getInstance().getProductData().register(product));
        return super.add(product);
    }
}
