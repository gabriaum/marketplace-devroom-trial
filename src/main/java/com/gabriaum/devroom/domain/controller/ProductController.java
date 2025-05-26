package com.gabriaum.devroom.domain.controller;

import com.gabriaum.devroom.MarketMain;
import com.gabriaum.devroom.product.Product;

import java.util.ArrayList;

public class ProductController extends ArrayList<Product> {

    @Override
    public boolean add(Product product) {
        MarketMain.getInstance().getProductData().register(product);
        return super.add(product);
    }
}
